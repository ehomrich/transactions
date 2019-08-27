(ns transactions.db.transaction)

(def tx-storage (atom []))

(defn save-transaction! [storage tx]
  (swap! storage conj tx))

(defn get-transaction-history [] @tx-storage)