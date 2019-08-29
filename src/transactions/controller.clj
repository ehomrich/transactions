(ns transactions.controller
  (:require
   [cheshire.core :refer [decode, encode]]
   [transactions.db.account :as account-db]
   [transactions.db.transaction :as transaction-db]
   [transactions.file.schemas :refer [account-schema transaction-schema valid-schema?]]
   [transactions.logic :as logic]))

(defn prepare-output
  "Formats output."
  [account violations]
  {:account account :violations violations})

(defn save-account!
  "Create and save an account, but first check if an account already exists."
  [data]
  (let [account (account-db/get-account)]
    (if (logic/account-exists? account)
      (prepare-output account ["illegal-account-reset"])
      (prepare-output (account-db/create-account! data) []))))

(defn update-account-limit!
  "Processes a transaction and updates the account limit."
  [account transaction]
  (let [{:keys [availableLimit]} account
        {:keys [amount]} transaction
        new-limit (- availableLimit amount)]
    (transaction-db/save-transaction! transaction)
    (account-db/update-account! {:availableLimit new-limit})))

(defn check-violations
  "Checks if the transaction violates any business rules."
  [account transaction tx-history]
  (cond
    (not (logic/account-exists? account)) "unknown-account"
    (not (logic/card-active? account)) "card-blocked"
    (not (logic/sufficient-limit? account transaction)) "insufficient-limit"
    (logic/doubled-transaction? tx-history transaction) "doubled-transaction"
    (logic/high-frequency-transactions? tx-history transaction) "high-frequency-small-interval"
    :else false))

(defn execute-transaction!
  "Attempts to execute a transaction."
  [transaction]
  (let [account (account-db/get-account)
        tx-history (transaction-db/get-transaction-history)]
    (if-let [result (check-violations account transaction tx-history)]
      (prepare-output account [result])
      (prepare-output (update-account-limit! account transaction) []))))

(defn validate-operation
  "Verifies the transaction type and redirects to the appropriate method."
  [data]
  (cond
    (valid-schema? account-schema data) (save-account! (:account data))
    (valid-schema? transaction-schema data) (execute-transaction! (:transaction data))
    :else (throw (java.lang.UnsupportedOperationException "Unrecognized input fields"))))

(defn process-data
  "Decodes the input, validates and process the operation and encodes the result."
  [data]
  (try
    (-> (decode data true)
        (validate-operation)
        (encode))
    ;; HACK: catch-all block because `cheshire` throws a myriad of exceptions.
    (catch Exception ex "")))