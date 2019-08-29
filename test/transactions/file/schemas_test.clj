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

(facts "valid-schema?"
       (fact "Successfully validates JSON schema"
             (valid-schema? product-schema product-input)
             => (contains product-input))

       (fact "Catch validation error"
             (valid-schema? product-schema {:name "Cheese"}) => false))
