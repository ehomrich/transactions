(ns transactions.logic)

(defn account-exists? [account]
  (seq account))

(defn card-active? [account]
  (:cardActive account))

(defn sufficient-limit? [account amount]
  (let [available-limit (:availableLimit account)]
    (> available-limit amount)))
