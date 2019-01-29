(defproject inflections "0.13.3-SNAPSHOT"
  :description "Rails-like inflections for Clojure(Script)."
  :url "http://github.com/r0man/inflections-clj"
  :author "r0man"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[noencore "0.3.6"]
                 [org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.439" :scope "provided"]]
  :aliases {"ci" ["do"
                  ["test"]
                  ["doo" "phantom" "none" "once"]
                  ["doo" "phantom" "advanced" "once"]
                  ["lint"]]
            "lint" ["do"  ["eastwood"]]}
  :cljsbuild {:builds [{:id "none"
                        :compiler
                        {:main inflections.test
                         :optimizations :none
                         :output-dir "target/none"
                         :output-to "target/none.js"}
                        :source-paths ["src" "test"]}
                       {:id "advanced"
                        :compiler
                        {:main inflections.test
                         :optimizations :advanced
                         :output-dir "target/advanced"
                         :output-to "target/advanced.js"}
                        :source-paths ["src" "test"]}]}
  :deploy-repositories [["releases" :clojars]]
  :profiles {:dev {:plugins [[jonase/eastwood "0.3.4"]
                             [lein-cljsbuild "1.1.7"]
                             [lein-difftest "2.0.0"]
                             [lein-doo "0.1.11"]]}})
