(ns transactions.controller
  (:require
   [transactions.file.schemas :refer [account-schema transaction-schema valid-schema?]]
   [transactions.file.serializer :refer [serialize]]))

(defn validate-operation [data]
  (cond
    (valid-schema? account-schema data) "is account"
    (valid-schema? transaction-schema data) "is transaction"
    :else (throw (java.lang.UnsupportedOperationException "Unrecognized input fields"))))

(defn process-data [data]
  (try
    (->> (serialize data)
         (validate-operation)
         (println))
        ;; HACK: "catch all" block because `cheshire` does not explicitly list 
        ;; exceptions that may occur.
    (catch Exception ex
      (println ""))))