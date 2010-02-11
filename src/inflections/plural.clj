(ns inflections.plural
  (:refer-clojure :exclude (replace))
  (:use [clojure.contrib.str-utils2 :only (blank? replace)]
        clojure.contrib.seq-utils
        inflections.helper
        inflections.rules
        inflections.uncountable))

(def *plural-rules* (atom []))

(defn plural
  "Define rule(s) to map words from singular to plural."
  [& patterns-and-replacements]
  (doseq [rule (apply map-rules patterns-and-replacements)]
    (add-rule! *plural-rules* rule)))

(defn pluralize
  "Returns the plural of the given word."
  [word]
  (if (or (blank? word) (uncountable? word))
    word
    (resolve-rules (rseq @*plural-rules*) word)))

(defn init-plural-rules []
  (reset-rules! *plural-rules*)
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

