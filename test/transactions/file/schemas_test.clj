(ns transactions.file.schemas-test
  (:require
   [midje.sweet :refer :all]
   [transactions.file.schemas :refer [valid-schema?
                                      account-schema
                                      transaction-schema]]))

(def product-schema {:$schema "http://json-schema.org/draft-07/schema#"
                     :type "object"
                     :properties {:name {:type "string"}
                                  :category {:type "string"}
                                  :price {:type "number"
                                          :exclusiveMinimum 0}}
                     :required [:name :category :price]})
(def product-input {:name "Butter" :category "Dairy" :price 1.20})
(def account-input {:account {:activeCard true
                              :availableLimit 117}})
(def transaction-input {:transaction {:merchant "KFC"
                                      :amount 87
                                      :time "2018-03-21T14:19:11.949Z"}})

(facts "valid-schema?"
       (fact "Successfully validates JSON schema"
             (valid-schema? product-schema product-input)
             => (contains product-input))

       (fact "Catch incomplete product data"
             (valid-schema? product-schema {:name "Cheese"}) => false))

(facts "Account and transaction schemas"
       (fact "Successfully validates account"
             (valid-schema? account-schema account-input)
             => (contains account-input))

       (fact "Successfully validates transaction"
             (valid-schema? transaction-schema transaction-input)
             => (contains transaction-input)))