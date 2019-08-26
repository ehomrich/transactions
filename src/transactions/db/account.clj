(ns transactions.db.account)

(def account (atom {}))

(defn create-account [account data]
  (reset! account data))

(defn update-account [account data]
  (swap! account merge data))

(defn get-account [] @account)