(ns transactions.controller-test
  (:require [midje.sweet :refer :all]
            [transactions.controller :as controller]))

(fact
 (some even? [1 5]) => falsey)