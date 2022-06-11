(defproject inflections "0.14.0-SNAPSHOT"
  :description "Rails-like inflections for Clojure(Script)."
  :url "http://github.com/r0man/inflections-clj"
  :author "r0man"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :aliases {"ci" ["do"
                  ["test"]
                  ["doo" "node" "none" "once"]
                  ["doo" "node" "advanced" "once"]
                  ["lint"]]
            "lint" ["do"  ["eastwood"]]}
  :cljsbuild {:builds [{:id "advanced"
                        :compiler
                        {:main inflections.test
                         :optimizations :none
                         :output-dir "target/advanced"
                         :output-to "target/advanced.js"
                         :parallel-build true
                         :pretty-print true
                         :target :nodejs
                         :verbose false}
                        :source-paths ["src" "test"]}
                       {:id "none"
                        :compiler
                        {:main inflections.test
                         :optimizations :advanced
                         :output-dir "target/none"
                         :output-to "target/none.js"
                         :target :nodejs}
                        :source-paths ["src" "test"]}]}
  :deploy-repositories [["releases" :clojars]]
  :profiles {:dev {:dependencies [[org.clojure/clojurescript "1.11.54"]]
                   :plugins [[jonase/eastwood "1.2.3"]
                             [lein-cljsbuild "1.1.7"]
                             [lein-difftest "2.0.0"]
                             [lein-doo "0.1.11"]]}})
