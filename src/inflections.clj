(ns inflections
  (:use [clojure.contrib.ns-utils :only (immigrate)]))

(immigrate
 'inflections.helper
 'inflections.irregular
 'inflections.uncountable)
