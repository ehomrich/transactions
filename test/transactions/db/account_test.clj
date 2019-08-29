(ns transactions.db.account-test
  (:require
   [midje.sweet :refer :all]
   [transactions.db.account :as account-db]))

(def account-sample {:activeCard true
                     :availableLimit 80})

(def update-sample {:availableLimit 50})

(facts "Account"
       (fact "Account not created yet"
             (account-db/get-account) => {})

       (fact "Create account"
             (account-db/create-account! account-sample) => account-sample)

       (fact "Update account limit"
             (account-db/update-account! update-sample)
             => (contains {:availableLimit 50})))