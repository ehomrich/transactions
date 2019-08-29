(ns transactions.db.account-test
  (:require
   [midje.sweet :refer :all]
   [transactions.db.account :as account-db]))

(def account-input {:activeCard true
                     :availableLimit 80})

(def update-account-input {:availableLimit 50})

(facts "Account"
       (fact "Account not created yet"
             (account-db/get-account) => {})

       (fact "Create account"
             (account-db/create-account! account-input) => account-input)

       (fact "Update account limit"
             (account-db/update-account! update-account-input)
             => (contains {:availableLimit 50})))