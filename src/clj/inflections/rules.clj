(ns inflections.rules
  (:refer-clojure :exclude [replace])
  (:use [clojure.string :only [replace]]))

(defrecord Rule [pattern replacement])

(defn add-rule! [rules rule]
  (if-not (contains? (set (deref rules)) rule)
    (swap! rules conj rule)))

(defn make-rule [pattern replacement]
  (Rule. pattern replacement))

(defn slurp-rules
  "Returns a seq of rules, where the pattern and replacement must be
  given in pairs of two elements."
  [& patterns-and-replacements]
  (map #(apply make-rule %) (partition 2 patterns-and-replacements)))

(defn resolve-rule [rule word]
  (let [pattern (:pattern rule)
        replacement (:replacement rule)]
    (if (re-find pattern word)
      (replace word pattern replacement))))

(defn resolve-rules [rules word]
  (first (remove nil? (map #(resolve-rule % word) rules))))

(defn reset-rules!
  "Resets the list of plural rules."
  [rules] (reset! rules []))
