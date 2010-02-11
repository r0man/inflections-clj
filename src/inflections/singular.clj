(ns inflections.singular
  (:refer-clojure :exclude (replace))
  (:use [clojure.contrib.str-utils2 :only (blank? replace)]
        clojure.contrib.seq-utils
        inflections.helper
        inflections.rules
        inflections.uncountable))

(def *singular-rules* (atom []))

(defn singular
  "Define rule(s) to map words from singular to plural."
  [& patterns-and-replacements]
  (doseq [rule (apply map-rules patterns-and-replacements)]
    (add-rule! *singular-rules* rule)))

(defn singularize
  "Returns the singular of the given word."
  [word]
  (if (or (blank? word) (uncountable? word))
    word
    (resolve-rules (rseq @*singular-rules*) word)))

(defn init-singular-rules []
  (reset-rules! *singular-rules*)
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
