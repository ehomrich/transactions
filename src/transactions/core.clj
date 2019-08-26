(ns transactions.core
  (:gen-class))

(defn -main
  "Read from sdtin."
  [& args]
  (->> (read-line)
       (println))
)