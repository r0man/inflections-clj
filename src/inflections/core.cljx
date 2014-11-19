(ns inflections.core
  (:refer-clojure :exclude [replace])
  (:require [clojure.string :refer [blank? lower-case upper-case replace split join]]
            [clojure.walk :refer [postwalk]]
            [no.en.core :refer [parse-integer]]))

;; RULES

(defrecord Rule [pattern replacement])

(defn add-rule! [rules rule]
  (if-not (contains? (set (deref rules)) rule)
    (swap! rules conj rule)))

(defn slurp-rules
  "Returns a seq of rules, where the pattern and replacement must be
  given in pairs of two elements."
  [& patterns-and-replacements]
  (map #(apply ->Rule %) (partition 2 patterns-and-replacements)))

(defn resolve-rule [rule word]
  (let [pattern (:pattern rule)
        replacement (:replacement rule)]
    (if (re-find pattern word)
      (replace word pattern replacement))))

(defn resolve-rules [rules word]
  (first (keep #(resolve-rule % word) rules)))

(defn reset-rules!
  "Resets the list of plural rules."
  [rules] (reset! rules []))

;; UNCOUNTABLE WORDS

(def ^{:dynamic true} *uncountable-words*
  (atom #{"air" "alcohol" "art" "blood" "butter" "cheese" "chewing" "coffee"
          "confusion" "cotton" "education" "electricity" "entertainment" "equipment"
          "experience" "fiction" "fish" "food" "forgiveness" "fresh" "gold" "gossip" "grass"
          "ground" "gum" "happiness" "history" "homework" "honey" "ice" "information" "jam"
          "knowledge" "lightning" "liquid" "literature" "love" "luck" "luggage" "meat" "milk"
          "mist" "money" "music" "news" "oil" "oxygen" "paper" "patience" "peanut" "pepper"
          "petrol" "pork" "power" "pressure" "research" "rice" "sadness" "series" "sheep"
          "shopping" "silver" "snow" "space" "species" "speed" "steam" "sugar" "sunshine" "tea"
          "tennis" "thunder" "time" "toothpaste" "traffic" "up" "vinegar" "washing" "wine"
          "wood" "wool"}))

(def ^{:dynamic true} *acronyms*
  (atom {"hst" "HST"
         "nasa" "NASA"}))

(defprotocol IAcronym
  (acronym [x] "Returns the correct version of the acronym if it is one, otherwise nil."))

(extend-protocol IAcronym
  #+clj clojure.lang.Keyword #+cljs cljs.core.Keyword
  (acronym [s]
    (acronym (name s)))
  #+clj clojure.lang.Symbol #+cljs cljs.core.Symbol
  (acronym [s]
    (acronym (str s)))
  #+clj java.lang.String #+cljs string
  (acronym [s]
    (get @*acronyms* (lower-case s))))

(defn add-acronym!
  "Adds `word` to the set of `*acronyms*`."
  [word] (swap! *acronyms* assoc (lower-case (name word)) (name word)))

(defn delete-acronym!
  "Delete `word` from the set of `*acronyms*`."
  [word] (swap! *acronyms* dissoc (lower-case (name word))))

(defprotocol ICountable
  (countable? [x] "Returns true if `x` is countable, otherwise false."))

(defn uncountable?
  "Returns true if `x` is uncountable, otherwise false."
  [x]
  (not (countable? x)))

(defn add-uncountable!
  "Adds `word` to the set of `*uncountable-words*`."
  [word] (swap! *uncountable-words* conj (lower-case (name word))))

(defn delete-uncountable!
  "Delete `word` from the set of `*uncountable-words*`."
  [word] (swap! *uncountable-words* disj (lower-case (name word))))

(extend-protocol ICountable
  #+clj clojure.lang.Keyword #+cljs cljs.core.Keyword
  (countable? [s]
    (countable? (name s)))
  #+clj clojure.lang.Symbol #+cljs cljs.core.Symbol
  (countable? [s]
    (countable? (str s)))
  #+clj java.lang.String #+cljs string
  (countable? [s]
    (not (contains? @*uncountable-words* (lower-case s)))))

;; PLURAL

(def ^{:dynamic true} *plural-rules*
  (atom []))

(defprotocol Plural
  (plural [x] "Returns the plural of x."))

(extend-protocol Plural
  #+clj clojure.lang.Keyword #+cljs cljs.core.Keyword
  (plural [k]
    (keyword (plural (name k))))
  #+clj clojure.lang.Symbol #+cljs cljs.core.Symbol
  (plural [k]
    (symbol (plural (name k))))
  #+clj java.lang.String #+cljs string
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

(defn init-plural-rules! []
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

;; SINGULAR

(def ^{:dynamic true} *singular-rules*
  (atom []))

(defprotocol Singular
  (singular [x] "Returns the singular of x."))

(extend-protocol Singular
  #+clj clojure.lang.Keyword #+cljs cljs.core.Keyword
  (singular [k]
    (keyword (singular (name k))))
  #+clj clojure.lang.Symbol #+cljs cljs.core.Symbol
  (singular [k]
    (symbol (singular (name k))))
  #+clj java.lang.String #+cljs string
  (singular [s]
    (if (uncountable? s)
      s (or (resolve-rules (rseq @*singular-rules*) s) s))))

(defn singular!
  "Define rule(s) to map words from singular to plural.\n
  Examples: (singular! #\"(n)ews$(?i)\" \"$1ews\")
            (singular! #\"(m)ovies$(?i)\" \"$1ovie\"
                       #\"([m|l])ice$(?i)\" \"$1ouse\")"
  [& patterns-and-replacements]
  (doseq [rule (apply slurp-rules patterns-and-replacements)]
    (add-rule! *singular-rules* rule)))

(defn init-singular-rules! []
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

;; IRREGULAR

(def ^{:dynamic true} *irregular-words*
  (atom (sorted-set)))

(defprotocol Irregular
  (irregular? [x]
    "Returns true if `x` is an irregular word, otherwise false."))

(defn add-irregular!
  "Add `singular` and `plural` to the set of `*irregular-words*`."
  [singular plural]
  (let [singular (lower-case (name singular))
        plural (lower-case (name plural))]
    (delete-uncountable! singular)
    (delete-uncountable! plural)
    (singular! (re-pattern (str "^" plural "$")) singular)
    (plural! (re-pattern (str "^" singular "$")) plural)
    (swap! *irregular-words* conj singular)
    (swap! *irregular-words* conj plural)))

(defn delete-irregular!
  "Delete `singular` and `plural` from the set of *irregular-words*."
  [singular plural]
  (let [singular (lower-case (name singular))
        plural (lower-case (name plural))]
    (swap! *irregular-words* disj singular)
    (swap! *irregular-words* disj plural)))

(extend-protocol Irregular
  #+clj clojure.lang.Keyword #+cljs cljs.core.Keyword
  (irregular? [k]
    (irregular? (name k)))
  #+clj clojure.lang.Symbol #+cljs cljs.core.Symbol
  (irregular? [k]
    (irregular? (name k)))
  #+clj java.lang.String #+cljs string
  (irregular? [s]
    (contains? @*irregular-words* (lower-case s))))

(defn init-irregular-words! []
  (doall
   (map #(add-irregular! (first %) (second %))
        [["amenity" "amenities"]
         ["child" "children"]
         ["cow" "kine"]
         ["foot" "feet"]
         ["louse" "lice"]
         ["mailman" "mailmen"]
         ["man" "men"]
         ["mouse" "mice"]
         ["move" "moves"]
         ["ox" "oxen"]
         ["person" "people"]
         ["sex" "sexes"]
         ["tooth" "teeth"]
         ["woman" "women"]])))


;; CAMEL-CASE

(defprotocol ICamelCase
  (-camel-case [x mode] "Camel-Case an x."))

(extend-protocol ICamelCase
  nil
  (-camel-case [_ _] nil)
  #+clj clojure.lang.Keyword #+cljs cljs.core.Keyword
  (-camel-case [x mode]
    (keyword (-camel-case (apply str (rest (str x))) mode)))
  #+clj clojure.lang.Symbol #+cljs cljs.core.Symbol
  (-camel-case [x mode]
    (symbol (-camel-case (str x) mode)))
  #+clj java.lang.String #+cljs string
  (-camel-case [x mode]
    (cond
     (= mode :lower) (-camel-case x lower-case)
     (= mode :upper) (-camel-case x upper-case)
     (fn? mode) (str (mode (str (first x)))
                     (apply str (rest (-camel-case x nil))))
     :else (-> (str x)
               (replace #"/(.?)" #(str "::" (upper-case (nth % 1))))
               (replace #"(^|_|-)(.)"
                        #+clj
                        #(str (if (#{\_ \-} (nth % 1))
                                (nth % 1))
                              (upper-case (nth % 2)))
                        #+cljs
                        #(let [[f r] %]
                           (str (if-not (#{\_ \-} f)
                                  (upper-case f))
                                (if r (upper-case r)))))))))

(defn camel-case
  "Convert `x` to camel case. By default, camel-case converts to
  UpperCamelCase. If the argument to camel-case is set to :lower then
  camel-case produces lowerCamelCase. The camel-case fn will also convert
  \"/\" to \"::\" which is useful for converting paths to namespaces.

  Examples:

    (camel-case \"active_record\")
    ;=> \"ActiveRecord\"

    (camel-case \"active_record\" :lower)
    ;=> \"activeRecord\"

    (camel-case \"active_record/errors\")
    ;=> \"ActiveRecord::Errors\"

    (camel-case \"active_record/errors\" :lower)
    ;=> \"activeRecord::Errors\""
  [x & [mode]] (-camel-case x mode))


;; CAPITALIZE

(defprotocol ICapitalize
  (-capitalize [x] "Capitalize an x."))

(defn upper-case? [x]
  (= x (upper-case x)))

(extend-protocol ICapitalize
  nil
  (-capitalize [_] nil)
  #+clj clojure.lang.Keyword #+cljs cljs.core.Keyword
  (-capitalize [x]
    (keyword (-capitalize (name x))))
  #+clj clojure.lang.Symbol #+cljs cljs.core.Symbol
  (-capitalize [x]
    (symbol (-capitalize (str x))))
  #+clj java.lang.String #+cljs string
  (-capitalize [x]
    (cond
      (acronym x) (acronym x)
      :else
      (str (upper-case (str (first x)))
           (when (next x) (lower-case (subs x 1)))))))


(defn capitalize
  "Convert the first letter in `x` to upper case.

  Examples:

    (capitalize \"hello\")
    ;=> \"Hello\"

    (capitalize \"HELLO\")
    ;=> \"Hello\"

    (capitalize \"abc123\")
    ;=> \"Abc123\""
  [x] (-capitalize x))

;; TITLEIZE

(defprotocol ITitleize
  (titleize [x]))

(extend-protocol ITitleize
  nil
  (titleize [_] nil)
  #+clj clojure.lang.Keyword #+cljs cljs.core.Keyword
  (titleize [x]
    (titleize (name x)))
  #+clj clojure.lang.Symbol #+cljs cljs.core.Symbol
  (titleize [x]
    (titleize (str x)))
  #+clj java.lang.String #+cljs string
  (titleize [x]
    (join " " (map capitalize (split (name x) #"[-_./ ]")))))

;; DASHERIZE

(defprotocol IDasherize
  (-dasherize [x] "Dasherize an x."))

(extend-protocol IDasherize
  nil
  (-dasherize [_] nil)
  #+clj clojure.lang.Keyword #+cljs cljs.core.Keyword
  (-dasherize [x]
    (keyword (-dasherize (name x))))
  #+clj clojure.lang.Symbol #+cljs cljs.core.Symbol
  (-dasherize [x]
    (symbol (-dasherize (str x))))
  #+clj java.lang.String #+cljs string
  (-dasherize [x]
    (replace x #"_" "-")))

(defn dasherize
  "Replaces all underscores in `x` with dashes.

  Examples:

    (dasherize \"puni_puni\")
    ;=> \"puni-puni\""
  [x] (-dasherize x))


;; DEMODULIZE

(defprotocol IDemodulize
  (-demodulize [x] "Demodulize an x."))

(extend-protocol IDemodulize
  nil
  (-demodulize [_] nil)
  #+clj clojure.lang.Keyword #+cljs cljs.core.Keyword
  (-demodulize [x]
    (keyword (-demodulize (name x))))
  #+clj clojure.lang.Symbol #+cljs cljs.core.Symbol
  (-demodulize [x]
    (symbol (-demodulize (str x))))
  #+clj java.lang.String #+cljs string
  (-demodulize [x]
    (replace x #"^.*(::|\.)" "")))

(defn demodulize
  "Removes the module part from `x`.

  Examples:

    (demodulize \"inflections.MyRecord\")
    ;=> \"MyRecord\"

    (demodulize \"ActiveRecord::CoreExtensions::String::Inflections\")
    ;=> \"Inflections\"

    (demodulize \"Inflections\")
    ;=> \"Inflections\""
  [x] (-demodulize x))

;; HYPHENATE

(defprotocol IHyphenate
  (-hyphenate [x] "Hyphenate an x."))

(extend-protocol IHyphenate
  nil
  (-hyphenate [_] nil)
  #+clj clojure.lang.Keyword #+cljs cljs.core.Keyword
  (-hyphenate [x]
    (keyword (-hyphenate (name x))))
  #+clj clojure.lang.Symbol #+cljs cljs.core.Symbol
  (-hyphenate [x]
    (symbol (-hyphenate (str x))))
  #+clj java.lang.String #+cljs string
  (-hyphenate [x]
    (-> (replace x #"::" "/")
        (replace #"([A-Z]+)([A-Z][a-z])" "$1-$2")
        (replace #"([a-z\d])([A-Z])" "$1-$2")
        (replace #"\s+" "-")
        (replace #"_" "-")
        (lower-case))))

(defn hyphenate
  "Hyphenate x, which is the same as threading `x` through the str,
  underscore and dasherize fns.

  Examples:

    (hyphenate 'Continent)
    ; => \"continent\"

    (hyphenate \"CountryFlag\")
    ; => \"country-flag\""
  [x] (-hyphenate x))

;; ORDINALIZE

(defprotocol IOrdinalize
  (-ordinalize [x] "Ordinalize an x."))

(extend-protocol IOrdinalize
  nil
  (-ordinalize [_] nil)
  #+clj clojure.lang.Keyword #+cljs cljs.core.Keyword
  (-ordinalize [x]
    (keyword (-ordinalize (name x))))
  #+clj clojure.lang.Symbol #+cljs cljs.core.Symbol
  (-ordinalize [x]
    (symbol (-ordinalize (str x))))
  #+clj java.lang.Number #+cljs number
  (-ordinalize [x]
    (-ordinalize (str x)))
  #+clj java.lang.String #+cljs string
  (-ordinalize [x]
    (if-let [number (parse-integer x)]
      (if (contains? (set (range 11 14)) (mod number 100))
        (str number "th")
        (let [modulus (mod number 10)]
          (cond
           (= modulus 1) (str number "st")
           (= modulus 2) (str number "nd")
           (= modulus 3) (str number "rd")
           :else (str number "th")))))))

(defn ordinalize
  "Turns `x` into an ordinal string used to denote the position in an
  ordered sequence such as 1st, 2nd, 3rd, 4th, etc.

  Examples:

    (ordinalize \"1\")
    ;=> \"1st\"

    (ordinalize \"23\")
    ;=> \"23rd\""
  [x] (-ordinalize x))

;; PARAMETERIZE

(defprotocol IParameterize
  (-parameterize [x sep] "Parameterize an x."))

(extend-protocol IParameterize
  nil
  (-parameterize [_ _] nil)
  #+clj clojure.lang.Keyword #+cljs cljs.core.Keyword
  (-parameterize [x sep]
    (keyword (-parameterize (name x) sep)))
  #+clj clojure.lang.Symbol #+cljs cljs.core.Symbol
  (-parameterize [x sep]
    (symbol (-parameterize (str x) sep)))
  #+clj java.lang.String #+cljs string
  (-parameterize [x sep]
    (let [sep (or sep "-")]
      (-> x
          #+clj (replace #"(?i)[^a-z0-9]+" sep)
          #+cljs (replace #"[^A-Za-z0-9]+" sep)
          (replace #"\++" sep)
          (replace (re-pattern (str sep "{2,}")) sep)
          (replace (re-pattern (str "(?i)(^" sep ")|(" sep "$)")) "")
          lower-case))))

(defn parameterize
  "Replaces special characters in `x` with the default separator
  \"-\". so that it may be used as part of a pretty URL.

  Examples:

    (parameterize \"Donald E. Knuth\")
    ; => \"donald-e-knuth\"

    (parameterize \"Donald E. Knuth\" \"_\")
    ; => \"donald_e_knuth\""
  [x & [sep]] (-parameterize x sep))

(defn pluralize
  "Attempts to pluralize the word unless count is 1. If plural is
  supplied, it will use that when count is > 1, otherwise it will use
  the inflector to determine the plural form."
  [count singular & [plural]]
  (str count " " (if (= 1 count) singular (or plural (inflections.core/plural singular)))))

;; UNDERSCORE

(defprotocol IUnderscore
  (-underscore [x] "Underscore an x."))

(extend-protocol IUnderscore
  nil
  (-underscore [_] nil)
  #+clj clojure.lang.Keyword #+cljs cljs.core.Keyword
  (-underscore [x]
    (keyword
     (if-let [ns (namespace x)]
       (-underscore ns))
     (-underscore (name x))))
  #+clj clojure.lang.Symbol #+cljs cljs.core.Symbol
  (-underscore [x]
    (symbol (-underscore (str x))))
  #+clj java.lang.String #+cljs string
  (-underscore [x]
    (-> x
        (replace #"::" "/")
        (replace #"([A-Z\d]+)([A-Z][a-z])" "$1_$2")
        (replace #"([a-z\d])([A-Z])" "$1_$2")
        (replace #"-" "_")
        lower-case)))

(defn underscore
  "The reverse of camel-case. Makes an underscored, lowercase form from
  the expression in the string. Changes \"::\" to \"/\" to convert
  namespaces to paths.

  Examples:

    (underscore \"ActiveRecord\")
    ;=> \"active_record\"

    (underscore \"ActiveRecord::Errors\")
    ;=> \"active_record/errors\""
  [x] (-underscore x))

;; FOREIGN KEY

(defprotocol IForeignKey
  (-foreign-key [x sep] "Demodulize an x."))

(extend-protocol IForeignKey
  nil
  (-foreign-key [_ _] nil)
  #+clj clojure.lang.Keyword #+cljs cljs.core.Keyword
  (-foreign-key [x sep]
    (keyword (-foreign-key (name x) sep)))
  #+clj clojure.lang.Symbol #+cljs cljs.core.Symbol
  (-foreign-key [x sep]
    (symbol (-foreign-key (str x) sep)))
  #+clj java.lang.String #+cljs string
  (-foreign-key [x sep]
    (if-not (blank? x)
      (str (underscore (hyphenate (singular (demodulize x))))
           (or sep "_") "id"))))

(defn foreign-key
  "Converts `x` into a foreign key. The default separator \"_\" is
  placed between the name and \"id\".


  Examples:

    (foreign-key \"Message\")
    ;=> \"message_id\"

    (foreign-key \"Message\" false)
    ;=> \"messageid\"

    (foreign-key \"Admin::Post\")
    ;=> \"post_id\""
  [x & [sep]] (-foreign-key x sep))

;; TRANSFORMATIONS ON MAPS

(defn transform-keys
  "Recursively transform all keys in the map `m` by applying `f` on them."
  [m f]
  (if (map? m)
    (reduce
     (fn [memo key]
       (let [value (get m key)]
         (-> (dissoc memo key)
             (assoc (f key)
               (cond
                (map? value) (transform-keys value f)
                (sequential? value) (map #(transform-keys % f) value)
                :else value)))))
     m (keys m))
    m))

(defn transform-values
  "Recursively transform all map values of m by applying f on them."
  [m f]
  (if (map? m)
    (reduce
     (fn [memo key]
       (let [value (get m key)]
         (assoc memo key (if (map? value) (transform-values value f) (f value)))))
     m (keys m))
    m))

(defn camel-case-keys
  "Recursively apply camel-case on all keys of m."
  [m & [mode]] (transform-keys m #(camel-case %1 mode)))

(defn hyphenate-keys
  "Recursively apply hyphenate on all keys of m."
  [m] (transform-keys m hyphenate))

(defn hyphenate-values
  "Recursively apply hyphenate on all values of m."
  [m] (transform-values m hyphenate))

(defn stringify-keys
  "Recursively transform all keys of m into strings."
  [m] (transform-keys m #(if (keyword? %) (name %) (str %))))

(defn stringify-values
  "Recursively transform all values of m into strings."
  [m] (transform-values m #(if (keyword? %) (name %) (str %))))

(defn underscore-keys
  "Recursively apply underscore on all keys of m."
  [m] (transform-keys m underscore))

(defn init-inflections!
  "Initialize the Inflections library with defaults."
  []
  (init-plural-rules!)
  (init-singular-rules!)
  (init-irregular-words!))

(init-inflections!)
