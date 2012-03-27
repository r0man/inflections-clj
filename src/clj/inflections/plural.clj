(ns inflections.plural
  (:use [clojure.string :only (blank?)]
        inflections.rules
        inflections.uncountable))

(def ^{:dynamic true} *plural-rules*
  (atom []))

(defprotocol Plural
  (plural [obj] "Returns the plural of obj."))

(extend-type clojure.lang.Keyword
  Plural
  (plural [k]
    (keyword (plural (name k)))))

(extend-type clojure.lang.Symbol
  Plural
  (plural [k]
    (symbol (plural (name k)))))

(extend-type String
  Plural
  (plural [s]
    (if (or (blank? s) (uncountable? s))
      s (resolve-rules (rseq @*plural-rules*) s))))

(defn plural!
  "Define rule(s) to map words from singular to plural.\n
  Examples: (plural! #\"$(?i)\" \"s\")
            (plural! #\"(ax|test)is$(?i)\" \"$1es\"
                     #\"(octop|vir)us$(?i)\" \"$1i\")"
  [& patterns-and-replacements]
  (doseq [rule (apply slurp-rules patterns-and-replacements)]
    (add-rule! *plural-rules* rule)))

(defn init-plural-rules []
  (plural!
   #"$(?i)" "s"
   #"s$(?i)" "s"
   #"(ax|test)is$(?i)" "$1es"
   #"(octop|vir)us$(?i)" "$1i"
   #"(alias|status)$(?i)" "$1es"
   #"(bu)s$(?i)" "$1ses"
   #"(buffal|tomat)o$(?i)" "$1oes"
   #"([ti])um$(?i)" "$1a"
   #"sis$(?i)" "ses"
   #"(?:([^f])fe|([lr])f)$(?i)" "$1$2ves"
   #"(hive)$(?i)" "$1s"
   #"([^aeiouy]|qu)y$(?i)" "$1ies"
   #"(x|ch|ss|sh)$(?i)" "$1es"
   #"(matr|vert|ind)(?:ix|ex)$(?i)" "$1ices"
   #"([m|l])ouse$(?i)" "$1ice"
   #"^(ox)$(?i)" "$1en"
   #"(quiz)$(?i)" "$1zes"))
