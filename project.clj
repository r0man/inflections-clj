(defproject inflections "0.8.1-SNAPSHOT"
  :description "Rails-like inflections for Clojure(Script)."
  :url "http://github.com/r0man/inflections-clj"
  :author "Roman Scherer"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.0"]]
  :profiles {:dev {:dependencies [[com.cemerick/clojurescript.test "0.0.1"]]}}
  :plugins [[lein-cljsbuild "0.3.0"]]
  :hooks [leiningen.cljsbuild]
  :cljsbuild {:builds [{:compiler {:output-to "target/inflections-test.js"
                                   :optimizations :whitespace
                                   :pretty-print true}
                        :source-paths ["test"]}
                       {:compiler {:output-to "target/inflections-debug.js"
                                   :optimizations :whitespace
                                   :pretty-print true}
                        :source-paths ["src"]}
                       {:compiler {:output-to "target/inflections.js"
                                   :optimizations :advanced
                                   :pretty-print false}
                        :source-paths ["src"]
                        :jar true}]
              :repl-listen-port 9000
              :repl-launch-commands
              {"chromium" ["chromium" "http://localhost:9000/"]
               "firefox" ["firefox" "http://localhost:9000/"]
               "phantomjs" ["phantomjs"
                            "resources/repl.js"
                            "resources/repl.html"
                            :stdout ".repl-phantom-naked-out"
                            :stderr ".repl-phantom-naked-err"]}
              :test-commands {"unit-tests" ["runners/phantomjs.js" "target/inflections-test.js"]}})
