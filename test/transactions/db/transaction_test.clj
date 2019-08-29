(ns transactions.db.transaction-test
  (:require
   [midje.sweet :refer :all]
   [transactions.db.transaction :as transaction-db]))

(namespace-state-changes [(before :contents (transaction-db/reset-transaction-history!))
                          (after :contents (transaction-db/reset-transaction-history!))])

(def tx-input {:merchant "McDonald's"
               :amount 35
               :time "2018-03-21T14:19:11.949Z"})

(facts "Transactions"
       (fact "Get empty transaction history"
             (transaction-db/get-transaction-history) => [])

       (fact "Add transaction"
             (transaction-db/save-transaction! tx-input) => (contains tx-input)))