(ns transactions.db.transaction)

(def tx-storage (atom []))

(defn save-transaction!
  "Add a transaction to history."
  [tx]
  (swap! tx-storage conj tx))

(defn get-transaction-history
  "Get transaction history."
  []
  @tx-storage)

(defn reset-transaction-history!
  "Clear transaction history. This is only used in tests."
  []
  (reset! tx-storage []))