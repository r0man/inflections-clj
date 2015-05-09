(defproject inflections "0.9.14"
  :description "Rails-like inflections for Clojure(Script)."
  :url "http://github.com/r0man/inflections-clj"
  :author "r0man"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-3165" :scope "provided"]
                 [noencore "0.1.20"]]
  :aliases {"cleantest" ["do" "clean," "cljx" "once," "test," "cljsbuild" "test"]
            "ci" ["do" ["cleantest"] ["lint"]]
            "lint" ["do"  ["eastwood"]]}
  :cljx {:builds [{:source-paths ["src"]
                   :output-path "target/classes"
                   :rules :clj}
                  {:source-paths ["src"]
                   :output-path "target/classes"
                   :rules :cljs}
                  {:source-paths ["test"]
                   :output-path "target/test-classes"
                   :rules :clj}
                  {:source-paths ["test"]
                   :output-path "target/test-classes"
                   :rules :cljs}]}
  :cljsbuild {:builds [{:source-paths ["target/classes" "target/test-classes"]
                        :compiler {:output-to "target/testable.js"
                                   :optimizations :advanced
                                   :pretty-print true}}]
              :test-commands {"node" ["node" :node-runner "target/testable.js"]
                              "phantom" ["phantomjs" :runner "target/testable.js"]}}
  :deploy-repositories [["releases" :clojars]]
  :prep-tasks [["cljx" "once"] "javac" "compile"]
  :profiles {:dev {:plugins [[com.cemerick/clojurescript.test "0.3.3"]
                             [com.cemerick/piggieback "0.2.1"]
                             [com.keminglabs/cljx "0.6.0"]
                             [jonase/eastwood "0.2.1"]
                             [lein-cljsbuild "1.0.5"]
                             [lein-difftest "2.0.0"]]
                   :repl-options {:nrepl-middleware [cljx.repl-middleware/wrap-cljx]}
                   :test-paths ["target/test-classes"]}})
