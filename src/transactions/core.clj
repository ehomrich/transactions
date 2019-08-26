(ns transactions.core
  (:require [cheshire.core :refer [decode]])
  (:gen-class))

(def account (atom nil))

(defn -main
  "Read from sdtin."
  [& args]
  (loop [line (read-line)]
    (when line
      (println (decode line true))
      (recur (read-line)))))