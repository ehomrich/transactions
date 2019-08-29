(ns transactions.core
  (:require
   [transactions.controller :refer [process-data]])
  (:gen-class))

(defn -main
  "Reads from stdin, triggers the controller and prints the results to stdout."
  [& args]
  (loop [line (read-line)]
    (when line
      (println (process-data line))
      (recur (read-line)))))