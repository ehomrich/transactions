(ns transactions.db.account-test
  (:require [midje.sweet :refer :all]
            [transactions.db.account :as account-db]))

(fact "Searching for an account not yet created"
 (account-db/get-account) => {})