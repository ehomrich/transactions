(ns transactions.db.transaction-test
  (:require [midje.sweet :refer :all]
            [transactions.db.transaction :as transaction-db]))

(fact
 (some even? [1 5]) => falsey)