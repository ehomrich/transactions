(ns transactions.core
  (:require [cheshire.core :refer [decode]])
  (:gen-class))

(def account (atom nil))

(defn -main
  "Read from sdtin."
  [& args]
  (loop [line (read-line)]
    (when line
      (try
        (println (decode line true))
        ;; HACK: "catch all" block because `cheshire` does not explicitly list 
        ;; exceptions that may occur.
        (catch Exception ex
          (println "")))
      (recur (read-line)))))