(ns #^{:doc "Rails-like inflections for Clojure."
       :author "Roman Scherer"}
  inflections
  (:use [clojure.contrib.ns-utils :only (immigrate)]))

(immigrate
 'inflections.irregular
 'inflections.plural
 'inflections.singular
 'inflections.transform
 'inflections.uncountable)

(defn init-inflections []
  (init-plural-rules)
  (init-singular-rules)
  (init-uncountable-words)
  (init-irregular-words))

(init-inflections)
