(defproject transactions "0.1.0-SNAPSHOT"
  :description "A function that authorizes transactions for a specific account"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}

  :plugins [[lein-cljfmt "0.6.4"]
            [lein-kibit "0.1.7"]
            [lein-midje "3.2.1"]
            [lein-cloverage "1.1.1"]]

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [cheshire "5.9.0"]
                 [luposlip/json-schema "0.1.7"]
                 [clj-time "0.15.2"]]

  :main ^{:skip-aot true} transactions.core
  :target-path "target/%s"

  :profiles {:dev {:aliases {"start" ["trampoline" "run" "-m" "transactions.core"]
                             "coverage" ["cloverage" "-r" ":midje" "-o" "coverage"]}
                   :dependencies [[midje "1.9.9"]]}
             :uberjar {:aot :all}})
