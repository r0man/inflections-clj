(defproject inflections "0.13.0-SNAPSHOT"
  :description "Rails-like inflections for Clojure(Script)."
  :url "http://github.com/r0man/inflections-clj"
  :author "r0man"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[noencore "0.3.3"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.456" :scope "provided"]]
  :aliases {"ci" ["do"
                  ["test"]
                  ["doo" "phantom" "none" "once"]
                  ["doo" "phantom" "advanced" "once"]
                  ["lint"]]
            "lint" ["do"  ["eastwood"]]}
  :cljsbuild {:builds [{:id "none"
                        :compiler
                        {:main 'inflections.test
                         :optimizations :none
                         :output-dir "target/none"
                         :output-to "target/none.js"
                         :pretty-print true
                         :verbose true}
                        :source-paths ["src" "test"]}
                       {:id "advanced"
                        :compiler
                        {:main 'inflections.test
                         :optimizations :advanced
                         :output-dir "target/advanced"
                         :output-to "target/advanced.js"
                         :pretty-print true
                         :verbose true}
                        :source-paths ["src" "test"]}]}
  :deploy-repositories [["releases" :clojars]]
  :profiles {:dev {:plugins [[com.cemerick/piggieback "0.2.1"]
                             [jonase/eastwood "0.2.3"]
                             [lein-cljsbuild "1.1.5"]
                             [lein-difftest "2.0.0"]
                             [lein-doo "0.1.7"]]}})
