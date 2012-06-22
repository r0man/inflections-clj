(ns inflections.plural
  (:use [clojure.string :only [blank?]]
        [inflections.rules :only [add-rule! resolve-rules slurp-rules]]
        [inflections.uncountable :only [uncountable?]]))

(def ^{:dynamic true} *plural-rules*
  (atom []))

(defprotocol Plural
  (plural [obj] "Returns the plural of obj."))

(extend-type string
  Plural
  (plural [s]
    (if (or (blank? s) (uncountable? s))
      s (resolve-rules (reverse @*plural-rules*) s))))

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
   #"(?i)$" "s"
   #"(?i)s$" "s"
   #"(?i)(ax|test)is$" "$1es"
   #"(?i)(octop|vir)us$" "$1i"
   #"(?i)(alias|status)$" "$1es"
   #"(?i)(bu)s$" "$1ses"
   #"(?i)(buffal|tomat)o$" "$1oes"
   #"(?i)([ti])um$" "$1a"
   #"(?i)sis$" "ses"
   #"(?i)(?:([^f])fe|([lr])f)$" "$1$2ves"
   #"(?i)(hive)$" "$1s"
   #"(?i)([^aeiouy]|qu)y$" "$1ies"
   #"(?i)(x|ch|ss|sh)$" "$1es"
   #"(?i)(matr|vert|ind)(?:ix|ex)$" "$1ices"
   #"(?i)([m|l])ouse$" "$1ice"
   #"(?i)^(ox)$" "$1en"
   #"(?i)(quiz)$" "$1zes"))
