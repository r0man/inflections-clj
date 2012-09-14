(ns inflections.singular
  (:use [clojure.string :only [blank?]]
        [inflections.rules :only [add-rule! resolve-rules slurp-rules]]
        [inflections.uncountable :only [uncountable?]]))

(def ^{:dynamic true} *singular-rules*
  (atom []))

(extend-type js/RegExp
  IEquiv
  (-equiv [o other]
    (-equiv (str o) (str other)))
  IHash
  (-hash [o] (hash (str o))))

(defprotocol Singular
  (singular [obj] "Returns the singular of obj."))

(extend-type string
  Singular
  (singular [s]
    (if (uncountable? s)
      s (or (resolve-rules (reverse @*singular-rules*) s) s))))

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
   #"(?i)s$" ""
   #"(?i)(ss)$" "$1"
   #"(?i)(n)ews$" "$1ews"
   #"(?i)([ti])a$" "$1um"
   #"(?i)((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)(sis|ses)$" "$1$2sis"
   #"(?i)(^analy)(sis|ses)$" "$1sis"
   #"(?i)([^f])ves$" "$1fe"
   #"(?i)(hive)s$" "$1"
   #"(?i)(tive)s$" "$1"
   #"(?i)([lr])ves$" "$1f"
   #"(?i)([^aeiouy]|qu)ies$" "$1y"
   #"(?i)(s)eries$" "$1eries"
   #"(?i)(m)ovies$" "$1ovie"
   #"(?i)(x|ch|ss|sh)es$" "$1"
   #"(?i)([m|l])ice$" "$1ouse"
   #"(?i)(bus)(es)?$" "$1"
   #"(?i)(o)es$" "$1"
   #"(?i)(shoe)s$" "$1"
   #"(?i)(cris|ax|test)(is|es)$" "$1is"
   #"(?i)(octop|vir)(us|i)$" "$1us"
   #"(?i)(alias|status)(es)?$" "$1"
   #"(?i)^(ox)en" "$1"
   #"(?i)(vert|ind)ices$" "$1ex"
   #"(?i)(matr)ices$" "$1ix"
   #"(?i)(quiz)zes$" "$1"
   #"(?i)(database)s$" "$1"))
