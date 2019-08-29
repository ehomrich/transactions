(ns transactions.core
  (:require
   [transactions.controller :refer [process-data]])
  (:gen-class))

(defn -main
  "Read from sdtin."
  [& args]
  (loop [line (read-line)]
    (when line
      (println (process-data line))
      (recur (read-line)))))