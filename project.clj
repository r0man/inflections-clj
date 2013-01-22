(defproject inflections "0.7.5"
  :description "Rails-like inflections for Clojure(Script)."
  :url "http://github.com/r0man/inflections-clj"
  :author "Roman Scherer"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]]
  :plugins [[lein-cljsbuild "0.2.10"]]
  :hooks [leiningen.cljsbuild]
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :cljsbuild {:builds [{:compiler {:output-to "target/inflections-test.js"
                                   :optimizations :advanced
                                   :pretty-print true}
                        :source-path "test/cljs"}
                       {:compiler {:output-to "target/inflections-debug.js"
                                   :optimizations :whitespace
                                   :pretty-print true}
                        :source-path "src/cljs"}
                       {:compiler {:output-to "target/inflections.js"
                                   :optimizations :advanced
                                   :pretty-print false}
                        :source-path "src/cljs"
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
              :test-commands {"phantomjs" ["bin/phantomjs-test"]
                              "v8" ["bin/v8-test"]}})
