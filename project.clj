(defproject inflections "0.8.2-SNAPSHOT"
  :description "Rails-like inflections for Clojure(Script)."
  :url "http://github.com/r0man/inflections-clj"
  :author "Roman Scherer"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-1853"]]
  :profiles {:dev {:dependencies [[com.cemerick/clojurescript.test "0.0.4"]]
                   :plugins [[com.cemerick/austin "0.1.0"]]}}
  :plugins [[lein-cljsbuild "0.3.2"]]
  :hooks [leiningen.cljsbuild]
  :cljsbuild {:builds [{:compiler {:output-to "target/inflections-test.js"
                                   :optimizations :advanced
                                   :pretty-print true}
                        :source-paths ["test"]}
                       {:compiler {:output-to "target/inflections-debug.js"
                                   :optimizations :whitespace
                                   :pretty-print true}
                        :source-paths ["src"]}
                       {:compiler {:output-to "target/inflections.js"
                                   :optimizations :advanced
                                   :pretty-print false}
                        :source-paths ["src"]}]
              :crossover-jar true
              :crossovers [inflections.core
                           inflections.string]
              :test-commands {"unit-tests" ["runners/phantomjs.js" "target/inflections-test.js"]}})
