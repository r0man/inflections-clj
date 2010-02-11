(ns inflections
  (:use [clojure.contrib.ns-utils :only (immigrate)]))

(immigrate
 'inflections.helper
 'inflections.irregular
 'inflections.plural
 'inflections.uncountable)

(defn load-rules []
  (require 'inflections.rules))

(load-rules)

(pluralize "woman")