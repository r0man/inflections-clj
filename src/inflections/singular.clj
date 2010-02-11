(ns inflections.singular
  (:refer-clojure :exclude (replace))
  (:use [clojure.contrib.str-utils2 :only (blank? replace)]
        clojure.contrib.seq-utils
        inflections.helper
        inflections.rules
        inflections.uncountable))

(def *singular-rules* (atom []))

(defn add-singular-rule
  "Adds the given pattern and replacement to the singularization rules."
  [pattern replacement]
  (if-not (includes? @*singular-rules* [pattern replacement])
    (swap! *singular-rules* conj [pattern replacement])))

(defn reset-singular-rules
  "Resets the list of singular rules."
  [] (reset! *singular-rules* []))

(defn singular
  "Define rule(s) to map from singular to singular."
  [& patterns-and-replacements]
  (assert-even-args patterns-and-replacements)
  (doseq [[pattern replacement] (partition 2 patterns-and-replacements)]
    (add-singular-rule pattern replacement)))

