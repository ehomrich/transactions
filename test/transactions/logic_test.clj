(ns transactions.logic-test
  (:require [midje.sweet :refer :all]
            [transactions.logic :as logic]))

(fact
 (some even? [1 5]) => falsey)