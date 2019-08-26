(ns transactions.core
  (:require [cheshire.core :refer [decode]]
            [json-schema.core :as json]
            [transactions.controller :refer [validate-operation]])
  (:gen-class))

(defn -main
  "Read from sdtin."
  [& args]
  (loop [line (read-line)]
    (when line
      (try
        (->> (decode line true)
             (validate-operation)
             (println))
        ;; HACK: "catch all" block because `cheshire` does not explicitly list 
        ;; exceptions that may occur.
        (catch Exception ex
          (println "")))
      (recur (read-line)))))