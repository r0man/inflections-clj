(ns inflections.rules
  (:refer-clojure :exclude (replace))
  (:use [clojure.contrib.str-utils2 :only (replace)]))

(defstruct rule :pattern :replacement)

(defn make-rule [pattern replacement]
  (struct rule pattern replacement))

(defn apply-rule [rule word]
  (if-let [{:keys [pattern replacement]} rule]
    (replace word pattern replacement)))

(defn apply-rules [rules word]
  (for [{:keys [pattern replacement]} rules
        :let [result (replace word pattern replacement)]
        :when (not (= word result))]
    result))
