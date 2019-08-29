(ns transactions.controller-test
  (:require
   [midje.sweet :refer :all]
   [transactions.controller :as ctrl]
   [transactions.db.account :as account-db]
   [transactions.db.transaction :as transaction-db]))

(def account {:activeCard true :availableLimit 200})

(def account-creation-input {:account account})

(def account-created-output {:account account :violations []})

(def tx-history [{:merchant "Subway" :amount 15 :time "2017-09-03T21:54:50.000Z"}
                 {:merchant "KFC" :amount 20 :time "2017-09-03T21:55:35.000Z"}
                 {:merchant "Subway" :amount 15 :time "2017-09-03T21:56:01.000Z"}
                 {:merchant "Bob's" :amount 25 :time "2017-09-03T22:19:00.000Z"}])

(def doubled-tx {:merchant "Subway"
                 :amount 15
                 :time "2017-09-03T21:56:15.000Z"})

(def high-frequency-tx {:merchant "Burger King"
                        :amount 50
                        :time "2017-09-03T21:56:38.000Z"})

(def tx-without-violations (last tx-history))

(def tx-input {:transaction tx-without-violations})

(fact "Prepare output"
      (ctrl/prepare-output {:foo true} ["card-blocked"])
      => (contains {:account {:foo true} :violations ["card-blocked"]}))

(facts "Account creation, update and violations"
       (with-state-changes [(before :contents (do
                                                (account-db/create-account! {})
                                                (transaction-db/reset-transaction-history!)))
                            (after :contents (do
                                               (account-db/create-account! {})
                                               (transaction-db/reset-transaction-history!)))]

         (fact "Account created successfully"
               (ctrl/save-account! account) => account-created-output)

         (fact "Limit updated successfully"
               (ctrl/update-account-limit! account tx-without-violations)
               => (contains {:availableLimit 175}))

         (fact "Illegal account reset"
               (ctrl/save-account! account)
               => (contains {:violations ["illegal-account-reset"]}))))

(facts "Transaction violation checks"
       (fact "Unknown account"
             (ctrl/check-violations {} nil nil) => "unknown-account")

       (fact "Card blocked"
             (ctrl/check-violations {:activeCard false} nil nil)
             => "card-blocked")

       (fact "Insufficient limit"
             (ctrl/check-violations account {:amount 500} nil)
             => "insufficient-limit")

       (fact "Doubled transaction"
             (ctrl/check-violations account doubled-tx tx-history)
             => "doubled-transaction")

       (fact "High frequency of transactions in a small interval"
             (ctrl/check-violations account high-frequency-tx tx-history)
             => "high-frequency-small-interval"))

(facts "Execute operation"
       (fact "Unrecognized input"
             (ctrl/validate-operation {})
             => (throws Exception)))

(facts "Validate operation"
       (with-state-changes [(before :contents (do
                                                (account-db/create-account! {})
                                                (transaction-db/reset-transaction-history!)))
                            (after :contents (do
                                               (account-db/create-account! {})
                                               (transaction-db/reset-transaction-history!)))]

         (fact "Unrecognized input"
               (ctrl/validate-operation {})
               => (throws Exception))

         (fact "Account creation operation"
               (ctrl/validate-operation account-creation-input)
               => (contains {:violations []}))

         (fact "Transaction operation"
               (ctrl/validate-operation tx-input)
               => (contains {:violations []}))))

(facts "Process data"
       (fact "Input parsing error"
             (ctrl/process-data "{") => ""))