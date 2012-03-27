(defproject inflections "0.7.0-SNAPSHOT"
  :description "Rails-like inflections for Clojure."
  :dependencies [[org.clojure/clojure "1.3.0"]]
  :author "Roman Scherer"
  :url "http://github.com/r0man/inflections-clj"
  :min-lein-version "2.0.0"
  :plugins [[lein-cljsbuild "0.1.3"]]
  :hooks [leiningen.cljsbuild]
  :cljsbuild {:builds [{:source-path "src"
                        :compiler {:output-to "target/inflections-debug.js"}}
                       {:source-path "src"
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
              :crossover-path "src/cljs"}
  :source-paths ["src/clj"]
  :test-paths ["test/clj"])
