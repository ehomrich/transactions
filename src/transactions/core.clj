(ns transactions.core
  (:require
   [transactions.controller :refer [process-data]])
  (:gen-class))

(defn -main
  "Read from sdtin."
  [& args]
  (loop [line (read-line)]
    (when line
      (process-data line)
      (recur (read-line)))))