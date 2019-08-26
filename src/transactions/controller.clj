(ns transactions.controller
  (:require 
   [transactions.file.schema :refer [account-schema transaction-schema valid-schema?]]))

(defn validate-operation [data]
  (cond
    (valid-schema? account-schema data) "is account"
    (valid-schema? transaction-schema data) "is transaction"
    :else (throw (java.lang.UnsupportedOperationException "Unrecognized input fields"))))