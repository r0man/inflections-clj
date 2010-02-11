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
  (let [rule (make-rule pattern replacement)]
    (if-not (includes? @*singular-rules* rule)
      (swap! *singular-rules* conj rule))))

(defn reset-singular-rules
  "Resets the list of singular rules."
  [] (reset! *singular-rules* []))

(defn singular
  "Define rule(s) to map from singular to singular."
  [& patterns-and-replacements]
  (assert-even-args patterns-and-replacements)
  (doseq [[pattern replacement] (partition 2 patterns-and-replacements)]
    (add-singular-rule pattern replacement)))

(defn singularize
  "Returns the singular of the given word."
  [word]
  (if (or (blank? word) (uncountable? word))
    word
    (first (apply-rules (rseq @*singular-rules*) word))))

(defn init-singular-rules []
  (reset-singular-rules)
  (singular
   #"s$" ""
   #"(n)ews$" "$1ews"
   #"([ti])a$" "$1um"
   #"((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$" "$1$2sis"
   #"(^analy)ses$" "$1sis"
   #"([^f])ves$" "$1fe"
   #"(hive)s$" "$1"
   #"(tive)s$" "$1"
   #"([lr])ves$" "$1f"
   #"([^aeiouy]|qu)ies$" "$1y"
   #"(s)eries$" "$1eries"
   #"(m)ovies$" "$1ovie"
   #"(x|ch|ss|sh)es$" "$1"
   #"([m|l])ice$" "$1ouse"
   #"(bus)es$" "$1"
   #"(o)es$" "$1"
   #"(shoe)s$" "$1"
   #"(cris|ax|test)es$" "$1is"
   #"(octop|vir)i$" "$1us"
   #"(alias|status)es$" "$1"
   #"^(ox)en" "$1"
   #"(vert|ind)ices$" "$1ex"
   #"(matr)ices$" "$1ix"
   #"(quiz)zes$" "$1"
   #"(database)s$" "$1"))
