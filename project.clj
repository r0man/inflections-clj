(defproject inflections "0.7.2"
  :description "Rails-like inflections for Clojure(Script)."
  :url "http://github.com/r0man/inflections-clj"
  :author "Roman Scherer"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.4.0"]]
  :plugins [[lein-cljsbuild "0.2.7"]]
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
               "firefox" ["firefox" "http://localhost:9000/"]}
              :test-commands {"unit" ["./test-cljs.sh"]}})
