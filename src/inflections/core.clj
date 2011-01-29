(ns #^{:author "Roman Scherer"
       :doc "Rails-like inflections for Clojure.

Examples:
user> (use 'inflections)
nil
user> (pluralize \"word\")
\"words\"
user> (pluralize \"virus\")
\"viri\"
user> (singularize \"apples\")
\"apple\"
user> (singularize \"octopi\")
\"octopus\"
user> (underscore \"puni-puni\")
\"puni_puni\"
user> (ordinalize \"52\")
\"52nd\"
user> (capitalize \"clojure\")
\"Clojure\"
"}
  inflections.core
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
