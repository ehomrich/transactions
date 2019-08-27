(ns transactions.controller
  (:require
   [transactions.db.account :as account-db]
   [transactions.db.transaction :as transaction-db]
   [transactions.file.schemas :refer [account-schema transaction-schema valid-schema?]]
   [transactions.file.serializer :refer [serialize]]
   [transactions.logic :as logic]))

(defn save-account! [data]
  (let [account (account-db/get-account)]
    (if (logic/account-exists? account)
      "illegal-account-reset"
      (account-db/create-account! data))))

(defn update-account-limit! [account transaction]
  (let [{:keys [availableLimit]} account
        {:keys [amount]} transaction
        new-limit (- availableLimit amount)]
    (transaction-db/save-transaction! transaction)
    (account-db/update-account! {:availableLimit new-limit})))

(defn check-violations [account transaction tx-history]
  (cond
    (not (logic/card-active? account)) "card-blocked"
    (not (logic/sufficient-limit? account transaction)) "insufficient-limit"
    (logic/doubled-transaction? tx-history transaction) "doubled-transaction"
    (logic/high-frequency-transactions? tx-history transaction) "high-frequency-small-interval"
    :else false))

(defn execute-transaction [transaction]
  (let [account (account-db/get-account)
        tx-history (transaction-db/get-transaction-history)]
    (if-let [result (check-violations account transaction tx-history)]
      result
      (update-account-limit! account transaction))))

(defn validate-operation [data]
  (cond
    (valid-schema? account-schema data) (save-account! (:account data))
    (valid-schema? transaction-schema data) (execute-transaction (:transaction data))
    :else (throw (java.lang.UnsupportedOperationException "Unrecognized input fields"))))

(defn process-data [data]
  (try
    (->> (serialize data)
         (validate-operation)
         (println))
        ;; HACK: "catch all" block because `cheshire` does not explicitly list 
        ;; exceptions that may occur.
    (catch Exception ex
      (println ex))))