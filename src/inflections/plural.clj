(ns inflections.plural
  (:refer-clojure :exclude (replace))
  (:use [clojure.contrib.str-utils2 :only (blank? replace)]
        clojure.contrib.seq-utils
        inflections.helper
        inflections.uncountable))

(def *plural-rules* (atom []))

(defn add-plural-rule
  "Adds the given pattern and replacement to the pluralization rules."
  [pattern replacement]
  (let [rule (make-rule pattern replacement)]
    (if-not (includes? @*plural-rules* rule)
      (swap! *plural-rules* conj rule))))

(defn reset-plural-rules
  "Resets the list of plural rules."
  [] (reset! *plural-rules* []))

(defn plural
  "Define rule(s) to map from singular to plural."
  [& patterns-and-replacements]
  (assert-even-args patterns-and-replacements)
  (doseq [[pattern replacement] (partition 2 patterns-and-replacements)]
    (add-plural-rule pattern replacement)))

(defn pluralize
  "Returns the plural of the given word."
  [word]
  (if (or (blank? word) (uncountable? word))
    word
    (first (apply-rules (rseq @*plural-rules*) word))))

(defn init-plural-rules []
  (reset-plural-rules)
  (plural
   #"$" "s"
   #"s$" "s"
   #"(ax|test)is$" "$1es"
   #"(octop|vir)us$" "$1i"
   #"(alias|status)$" "$1es"
   #"(bu)s$" "$1ses"
   #"(buffal|tomat)o$" "$1oes"
   #"([ti])um$" "$1a"
   #"sis$" "ses"
   #"(?:([^f])fe|([lr])f)$" "$1$2ves"
   #"(hive)$" "$1s"
   #"([^aeiouy]|qu)y$" "$1ies"
   #"(x|ch|ss|sh)$" "$1es"
   #"(matr|vert|ind)(?:ix|ex)$" "$1ices"
   #"([m|l])ouse$" "$1ice"
   #"^(ox)$" "$1en"
   #"(quiz)$" "$1zes"))

