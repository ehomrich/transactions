(ns transactions.file.schemas-test
  (:require [midje.sweet :refer :all]
            [transactions.file.schemas :as schemas]))

(fact
 (some even? [1 5]) => falsey)