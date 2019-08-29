(ns transactions.logic
  (:require
   [clj-time [core :as time] [local :as local-time]]))

(defn account-exists?
  "Check if the account has been created."
  [account]
  (seq account))

(defn card-active?
  "Checks if the account card is active or blocked."
  [account]
  (:activeCard account))

(defn sufficient-limit?
  "Checks whether the available account limit is sufficient for the 
  transaction amount."
  [account transaction]
  (let [{:keys [availableLimit]} account
        {:keys [amount]} transaction]
    (>= availableLimit amount)))

(defn parse-date
  "Parses a ISO date-time string into an object."
  [dt]
  (local-time/to-local-date-time dt))

(defn get-time-interval
  "Creates a date interval, having `end-date `as the end of the interval.
  The start date is obtained by subtracting the `delta` from the end date.
  The end date is increased by 1 ms so that the original end limit is included
  in range comparisons."
  [end-limit delta]
  (let [base-date (parse-date end-limit)
        start (time/minus base-date (time/minutes delta))
        end (time/plus base-date (time/millis 1))]
    (time/interval start end)))

(defn within-interval?
  "Checks if the `interval` contains the entered date."
  [interval dt]
  (time/within? interval dt))

(defn get-transactions-in-time-interval
  "Search for transactions within the timeframe between the transaction to be
  authorized and a delta of minutes in the past."
  [tx-history transaction delta]
  (let [{:keys [time]} transaction
        interval (get-time-interval time delta)]
    (filter (fn [tx]
              (within-interval? interval (parse-date (:time tx)))) tx-history)))

(defn get-similar-transactions [tx-history transaction]
  "Search for transactions with the same merchant and amount."
  (let [{:keys [merchant amount]} transaction]
    (filter (fn [tx]
              (and (= amount (:amount tx))
                   (= merchant (:merchant tx))))
            tx-history)))

(defn doubled-transaction?
  "Checks for 2 or more similar transactions (same merchant and amount)
  in the last 2 minutes."
  [tx-history transaction]
  (-> (get-similar-transactions tx-history transaction)
      (get-transactions-in-time-interval transaction 2)
      (count)
      (>= 2)))

(defn high-frequency-transactions?
  "Checks for 3 or more authorized transactions in the last 2 minutes."
  [tx-history transaction]
  (-> (get-transactions-in-time-interval tx-history transaction 2)
      (count)
      (>= 3)))