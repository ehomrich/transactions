(ns transactions.controller-test
  (:require [midje.sweet :refer :all]
            [transactions.controller :as ctrl]))

(fact "Prepare output"
      (ctrl/prepare-output {:foo true} ["card-blocked"]) 
      => (contains {:accounts {:foo true} :violations ["card-blocked"]}))

(facts "Violation checks"
       (fact "Unknown account"
             (ctrl/check-violations {} nil nil) => "unknown-account")

       (fact "Card blocked"
             (ctrl/check-violations {:activeCard false} nil nil)
             => "card-blocked"))

(facts "Validate operation"
       (fact "Unrecognized input"
             (ctrl/validate-operation {})
             => (throws Exception)))

(facts "Process data"
       (fact "Input parsing error"
             (ctrl/process-data "{") => ""))