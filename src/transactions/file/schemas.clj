(ns transactions.file.schemas
  (:require [json-schema.core :as json]))

(def account-schema {:$schema "http://json-schema.org/draft-07/schema#"
                     :id "https://example.com/account-schema.json"
                     :type "object"
                     :properties {:account {:type "object"
                                            :properties {:activeCard {:type "boolean"}
                                                         :availableLimit {:type "number"
                                                                          :minimum 0}}
                                            :required [:activeCard :availableLimit]}}
                     :required [:account]})

(def transaction-schema {:$schema "http://json-schema.org/draft-07/schema#"
                         :id "https://example.com/transaction-schema.json"
                         :type "object"
                         :properties {:transaction {:type "object"
                                                    :properties {:merchant {:type "string"}
                                                                 :amount {:type "number"
                                                                          :minimum 0}
                                                                 :time {:type "string"}}
                                                    :required [:merchant :amount :time]}}
                         :required [:transaction]})

(defn valid-schema?
  "Checks if `input` meets `schema` requirements."
  [schema input]
  (try
    (json/validate schema input)
    (catch clojure.lang.ExceptionInfo ex false)))