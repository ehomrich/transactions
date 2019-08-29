(ns transactions.logic-test
  (:require
   [midje.sweet :refer :all]
   [clj-time [core :as time] [local :as local]]
   [transactions.logic :as logic]))

(def active-account {:activeCard true :availableLimit 55})
(def blocked-card {:activeCard false :availableLimit 55})
(def transaction {:merchant "Subway"
                  :amount 35
                  :time "2017-09-03T21:56:42.809Z"})
(def expensive-tx {:amount 1100})
(def tx-history [{:merchant "Subway" :amount 35 :time "2017-09-03T21:54:50.000Z"}
                 {:merchant "KFC" :amount 40 :time "2017-09-03T21:55:35.000Z"}
                 {:merchant "Subway" :amount 35 :time "2017-09-03T21:56:21.000Z"}
                 {:merchant "Bob's" :amount 50 :time "2017-09-03T22:19:00.000Z"}])
(def tx-without-violations (last tx-history))
(def dt-within-interval (local/to-local-date-time "2017-09-03T21:55:00.000Z"))
(def dt-out-of-interval (local/to-local-date-time "2017-09-03T22:05:00.000Z"))
(def some-interval (let [start (time/minus dt-within-interval (time/minutes 5))
                         base-end (time/plus start (time/minutes 10))
                         end (time/plus base-end (time/millis 1))]
                     (time/interval start end)))

(facts "Account state"
       (fact "Account does not exist"
             (logic/account-exists? {}) => falsey)

       (fact "Account exists"
             (logic/account-exists? active-account) => truthy))

(facts "Card state"
       (fact "Active card"
             (logic/card-active? active-account) => true)

       (fact "Blocked card"
             (logic/card-active? blocked-card) => false))

(facts "Limit check"
       (fact "Sufficient limit"
             (logic/sufficient-limit? active-account transaction) => true)

       (fact "Insuficient limit"
             (logic/sufficient-limit? active-account expensive-tx) => false))

(facts "Date-time parsing"
       (fact "Date-time parsed successfully"
             (logic/parse-date "2017-09-03T21:56:02.809Z") => truthy)

       (fact "Fail to parse date-time"
             (logic/parse-date "2017-09-0") => falsey))

(facts "Time interval"
       (fact "Interval created successfully"
             (logic/get-time-interval "2017-09-03T22:00:00.000Z" 10)
             => some-interval)

       (fact "Date is within interval"
             (logic/within-interval? some-interval dt-within-interval) => true)

       (fact "Date is out of interval"
             (logic/within-interval? some-interval dt-out-of-interval)
             => false))

(facts "Double transaction"
       (fact "The transaction has recent similar occurrences"
             (logic/doubled-transaction? tx-history transaction)
             => true)

       (fact "Transaction without recent occurrences"
             (logic/doubled-transaction? tx-history tx-without-violations)
             => false))

(facts "High frequency transaction"
       (fact "There was a high frequency of transactions in a short time"
             (logic/high-frequency-transactions? tx-history transaction)
             => true)

       (fact "No recent transactions"
             (logic/high-frequency-transactions? tx-history
                                                 tx-without-violations)
             => false))