(ns inflections.singular
  (:use [clojure.contrib.str-utils2 :only (blank?)]
        inflections.rules
        inflections.uncountable))

(def *singular-rules* (atom []))

(defn singular!
  "Define rule(s) to map words from singular to plural.\n
  Examples: (singular! #\"(n)ews$(?i)\" \"$1ews\")
            (singular! #\"(m)ovies$(?i)\" \"$1ovie\"
                       #\"([m|l])ice$(?i)\" \"$1ouse\")"
  [& patterns-and-replacements]
  (doseq [rule (apply slurp-rules patterns-and-replacements)]
    (add-rule! *singular-rules* rule)))

(defn singularize
  "Returns the singular of the given word.\n
  Example: (singularize \"mice\") => \"mouse\""
  [word]
  (if (or (blank? word) (uncountable? word))
    word
    (resolve-rules (rseq @*singular-rules*) word)))

(defn reset-singular-rules!
  "Resets the rules used to map from singular to plural."
  [] (reset-rules! *singular-rules*))

(defn init-singular-rules []
  (reset-singular-rules!)
  (singular!
   #"s$(?i)" ""
   #"(n)ews$(?i)" "$1ews"
   #"([ti])a$(?i)" "$1um"
   #"((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$(?i)" "$1$2sis"
   #"(^analy)ses$(?i)" "$1sis"
   #"([^f])ves$(?i)" "$1fe"
   #"(hive)s$(?i)" "$1"
   #"(tive)s$(?i)" "$1"
   #"([lr])ves$(?i)" "$1f"
   #"([^aeiouy]|qu)ies$(?i)" "$1y"
   #"(s)eries$(?i)" "$1eries"
   #"(m)ovies$(?i)" "$1ovie"
   #"(x|ch|ss|sh)es$(?i)" "$1"
   #"([m|l])ice$(?i)" "$1ouse"
   #"(bus)es$(?i)" "$1"
   #"(o)es$(?i)" "$1"
   #"(shoe)s$(?i)" "$1"
   #"(cris|ax|test)es$(?i)" "$1is"
   #"(octop|vir)i$(?i)" "$1us"
   #"(alias|status)es$(?i)" "$1"
   #"^(ox)en(?i)" "$1"
   #"(vert|ind)ices$(?i)" "$1ex"
   #"(matr)ices$(?i)" "$1ix"
   #"(quiz)zes$(?i)" "$1"
   #"(database)s$(?i)" "$1"))
