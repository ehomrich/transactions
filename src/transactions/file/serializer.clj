(ns transactions.file.serializer
  (:require [cheshire.core :refer [decode]]))

(defn serialize [input]
  (decode input true))