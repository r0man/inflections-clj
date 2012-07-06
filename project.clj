(defproject inflections "0.7.0-SNAPSHOT"
  :description "Rails-like inflections for Clojure(Script)."
  :url "http://github.com/r0man/inflections-clj"
  :author "Roman Scherer"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/clojurescript "0.0-1443"]]
  :plugins [[lein-cljsbuild "0.2.2"]]
  :hooks [leiningen.cljsbuild]
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
               "firefox" ["firefox" "http://localhost:9000/"]}}
  :source-paths ["src/clj"]
  :test-paths ["test/clj"])
