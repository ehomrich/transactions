(ns transactions.logic
  (:require
   [clj-time [core :as time] [local :as local-time]]))

(defn account-exists? [account]
  (seq account))

(defn card-active? [account]
  (:activeCard account))

(defn sufficient-limit? [account transaction]
  (let [{:keys [availableLimit]} account
        {:keys [amount]} transaction]
    (>= availableLimit amount)))

(defn parse-date [dt]
  (local-time/to-local-date-time dt))

(defn get-time-interval [base delta]
  (let [base-date (parse-date base)
        start (time/minus base-date (time/minutes delta))
        end (time/plus base-date (time/millis 1))]
    (time/interval start end)))

(defn within-interval? [interval dt]
  (time/within? interval dt))

(defn get-transactions-in-time-interval [tx-history transaction delta]
  (let [{:keys [time]} transaction
        interval (get-time-interval time delta)]
    (filter (fn [tx]
              (within-interval? interval (parse-date (:time tx)))) tx-history)))

(defn get-similar-transactions [tx-history transaction]
  (let [{:keys [merchant amount]} transaction]
    (filter (fn [tx]
              (and (= amount (:amount tx))
                   (= merchant (:merchant tx)))) tx-history)))

(defn doubled-transaction? [tx-history transaction]
  (-> (get-similar-transactions tx-history transaction)
      (get-transactions-in-time-interval transaction 2)
      (count)
      (>= 2)))

(defn high-frequency-transactions? [tx-history transaction]
  (-> (get-transactions-in-time-interval tx-history transaction 2)
      (count)
      (>= 3)))