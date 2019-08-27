(ns transactions.db.account)

(def account (atom {}))

(defn create-account! [data]
  (reset! account data))

(defn update-account! [data]
  (swap! account merge data))

(defn get-account [] @account)