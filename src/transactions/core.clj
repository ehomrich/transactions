(ns transactions.core
  (:require [cheshire.core :refer [decode]]
            [json-schema.core :as json])
  (:gen-class))

(def account (atom nil))

(defn create-account [state, input]
  (reset! state input))

(defn account-exists? [account]
  (not (seq account)))

(def account-schema {:$schema "http://json-schema.org/draft-07/schema#"
                     :id "https://example.com/account-schema.json"
                     :type "object"
                     :properties {:account {:type "object"
                                            :properties {:activeCard {:type "boolean"}
                                                         :availableLimit {:type "number"}}
                                            :required [:activeCard :availableLimit]}}
                     :required [:account]})

(def transaction-schema {:$schema "http://json-schema.org/draft-07/schema#"
                         :id "https://example.com/transaction-schema.json"
                         :type "object"
                         :properties {:transaction {:type "object"
                                                    :properties {:merchant {:type "string"}
                                                                 :amount {:type "number"}
                                                                 :time {:type "string"}}
                                                    :required [:merchant :amount :time]}}
                         :required [:transaction]})

(defn valid-input? [schema, input]
  (try
    (json/validate schema input)
    (catch clojure.lang.ExceptionInfo ex false)))

(defn validate-schema [data]
  (cond
    (valid-input? account-schema data) "is account"
    (valid-input? transaction-schema data) "is transaction"
    :else (throw (java.lang.UnsupportedOperationException "Unrecognized input fields"))))

(defn -main
  "Read from sdtin."
  [& args]
  (loop [line (read-line)]
    (when line
      (try
        (let [input (decode line true)]
          (println (validate-schema input)))
        ;; HACK: "catch all" block because `cheshire` does not explicitly list 
        ;; exceptions that may occur.
        (catch Exception ex
          (println "")))
      (recur (read-line)))))