(ns transactions.logic)

(defn account-exists? [account]
  (seq account))

(defn card-active? [account]
  (:cardActive account))

(defn sufficient-limit? [account amount]
  (-> (:availableLimit account)
      (>= amount)))

(defn get-similar-transactions [tx-history transaction]
  (let [{:keys [merchant amount]} transaction]
    (filter (fn [tx]
              (and (= amount (:amount tx))
                   (= merchant (:merchant tx)))) tx-history)))
