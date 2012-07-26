(defproject nigel "1.0.0-SNAPSHOT"
            :description "Nigel Migraine - Natural Language Generator"
            :dependencies [[org.clojure/clojure "1.3.0"]
                           [midje "1.4.0"]]
            :profiles {:dev {:plugins [[lein-midje "2.0.0-SNAPSHOT"]]}}
            :main nigel.core)