(ns inflections.singular
  (:use [clojure.string :only (blank?)]
        inflections.rules
        inflections.uncountable))

(def ^{:dynamic true} *singular-rules*
  (atom []))

(defprotocol Singular
  (singular [obj] "Returns the singular of obj."))

(extend-type clojure.lang.Keyword
  Singular
  (singular [k]
    (keyword (singular (name k)))))

(extend-type clojure.lang.Symbol
  Singular
  (singular [k]
    (symbol (singular (name k)))))

(extend-type String
  Singular
  (singular [s]
    (if (or (blank? s) (uncountable? s))
      s (resolve-rules (rseq @*singular-rules*) s))))

(defn singular!
  "Define rule(s) to map words from singular to plural.\n
  Examples: (singular! #\"(n)ews$(?i)\" \"$1ews\")
            (singular! #\"(m)ovies$(?i)\" \"$1ovie\"
                       #\"([m|l])ice$(?i)\" \"$1ouse\")"
  [& patterns-and-replacements]
  (doseq [rule (apply slurp-rules patterns-and-replacements)]
    (add-rule! *singular-rules* rule)))

(defn init-singular-rules []
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
