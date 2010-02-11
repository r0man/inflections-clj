(ns inflections
  (:use [clojure.contrib.ns-utils :only (immigrate)]))

(immigrate
 'inflections.helper
 'inflections.irregular
 'inflections.plural
 'inflections.singular
 'inflections.uncountable)

(defn init-inflections []
  (init-plural-rules)
  (init-singular-rules)
  (init-uncountable-words)
  (init-irregular-words))

(init-inflections)
