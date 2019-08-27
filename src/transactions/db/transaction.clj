(ns transactions.db.transaction)

(def tx-storage (atom []))

(defn save-transaction! [tx]
  (swap! tx-storage conj tx))

(defn get-transaction-history [] @tx-storage)