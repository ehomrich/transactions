(ns transactions.db.account)

(def account (atom {}))

(defn create-account!
  "Sets the account state."
  [data]
  (reset! account data))

(defn update-account!
  "Updates account properties.
  `data` must be a property map."
  [data]
  (swap! account merge data))

(defn get-account
  "Get account state."
  []
  @account)