(defproject inflections "0.7.0-SNAPSHOT"
  :description "Rails-like inflections for Clojure."
  :dependencies [[org.clojure/clojure "1.4.0"]]
  :author "Roman Scherer"
  :url "http://github.com/r0man/inflections-clj"
  :plugins [[lein-cljsbuild "0.1.3"]]
  ;; :hooks [leiningen.cljsbuild]
  :cljsbuild {:builds [{:source-path "src/cljs"
                        :compiler {:output-to "target/inflections-debug.js"}}
                       {:source-path "src/cljs"
                        :compiler {:output-to "target/inflections.js"
                                   :optimizations :advanced
                                   :pretty-print false}
                        :jar true}]
              :crossovers [inflections.core
                           inflections.irregular
                           inflections.plural
                           inflections.rules
                           inflections.singular
                           inflections.transform
                           inflections.uncountable]
              :crossover-jar true
              :repl-listen-port 9000
              :repl-launch-commands
              {"chromium" ["chromium" "test-resources/index.html"]
               "firefox" ["firefox" "test-resources/index.html"]}}
  :source-paths ["src/clj"]
  :test-paths ["test/clj"])
