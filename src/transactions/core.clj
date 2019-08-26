(ns transactions.core
  (:gen-class))

(defn -main
  "Read from sdtin."
  [& args]
  (loop [line (read-line)]
    (when line
      (println line)
      (recur (read-line)))))