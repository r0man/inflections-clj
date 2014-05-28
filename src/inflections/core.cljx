(ns inflections.core
  (:refer-clojure :exclude [replace])
  (:require [clojure.string :refer [blank? lower-case upper-case replace]]
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
  (first (remove nil? (map #(resolve-rule % word) rules))))

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

(defprotocol ICountable
  (countable? [obj] "Returns true if obj is countable, otherwise false."))

(defn uncountable?
  "Returns true if obj is uncountable, otherwise false."
  [obj]
  (not (countable? obj)))

(defn add-uncountable!
  "Adds `word` to the set of `*uncountable-words*`."
  [word] (swap! *uncountable-words* conj (lower-case (name word))))

(defn delete-uncountable!
  "Delete `word` from the set of `*uncountable-words*`."
  [word] (swap! *uncountable-words* disj (lower-case (name word))))

(extend-protocol ICountable
  #+clj clojure.lang.Keyword
  #+cljs cljs.core/Keyword
  (countable? [s]
    (countable? (name s)))
  #+clj clojure.lang.Symbol
  #+cljs cljs.core/Symbol
  (countable? [s]
    (countable? (str s)))
  #+clj java.lang.String
  #+cljs string
  (countable? [s]
    (not (contains? @*uncountable-words* (lower-case s)))))

;; PLURAL

(def ^{:dynamic true} *plural-rules*
  (atom []))

(defprotocol Plural
  (plural [obj] "Returns the plural of obj."))

(extend-protocol Plural
  #+clj clojure.lang.Keyword
  #+cljs cljs.core/Keyword
  (plural [k]
    (keyword (plural (name k))))
  #+clj clojure.lang.Symbol
  #+cljs cljs.core/Symbol
  (plural [k]
    (symbol (plural (name k))))
  #+clj java.lang.String
  #+cljs string
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
  (singular [obj] "Returns the singular of obj."))

(extend-protocol Singular
  #+clj clojure.lang.Keyword
  #+cljs cljs.core/Keyword
  (singular [k]
    (keyword (singular (name k))))
  #+clj clojure.lang.Symbol
  #+cljs cljs.core/Symbol
  (singular [k]
    (symbol (singular (name k))))
  #+clj java.lang.String
  #+cljs string
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
  (irregular? [obj]
    "Returns true if obj is an irregular word, otherwise false."))

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
  #+clj clojure.lang.Keyword
  #+cljs cljs.core/Keyword
  (irregular? [k]
    (irregular? (name k)))
  #+clj clojure.lang.Symbol
  #+cljs cljs.core/Symbol
  (irregular? [k]
    (irregular? (name k)))
  #+clj java.lang.String
  #+cljs string
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

(defprotocol ICamel-Case
  (-camel-case [object mode] "Camel-Case an object."))

(extend-protocol ICamel-Case
  nil
  (-camel-case [_ _] nil)
  #+clj clojure.lang.Keyword
  #+cljs cljs.core/Keyword
  (-camel-case [obj mode]
    (keyword (-camel-case (apply str (rest (str obj))) mode)))
  #+clj clojure.lang.Symbol
  #+cljs cljs.core/Symbol
  (-camel-case [obj mode]
    (symbol (-camel-case (str obj) mode)))
  #+clj java.lang.String
  #+cljs string
  (-camel-case [obj mode]
    (cond
     (= mode :lower) (-camel-case obj lower-case)
     (= mode :upper) (-camel-case obj upper-case)
     (fn? mode) (str (mode (str (first obj)))
                     (apply str (rest (-camel-case obj nil))))
     :else (-> (str obj)
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
  "Convert obj to camel case. By default, camel-case converts to
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
  [obj & [mode]] (-camel-case obj mode))


;; CAPITALIZE

(defprotocol ICapitalize
  (-capitalize [object] "Capitalize an object."))

(extend-protocol ICapitalize
  nil
  (-capitalize [_] nil)
  #+clj clojure.lang.Keyword
  #+cljs cljs.core/Keyword
  (-capitalize [obj]
    (keyword (-capitalize (name obj))))
  #+clj clojure.lang.Symbol
  #+cljs cljs.core/Symbol
  (-capitalize [obj]
    (symbol (-capitalize (str obj))))
  #+clj java.lang.String
  #+cljs string
  (-capitalize [obj]
    (str (upper-case (str (first obj)))
         (lower-case (apply str (rest obj))))))

(defn capitalize
  "Convert the first letter in obj to upper case.

  Examples:

    (capitalize \"hello\")
    ;=> \"Hello\"

    (capitalize \"HELLO\")
    ;=> \"Hello\"

    (capitalize \"abc123\")
    ;=> \"Abc123\""
  [obj] (-capitalize obj))

;; DASHERIZE

(defprotocol IDasherize
  (-dasherize [object] "Dasherize an object."))

(extend-protocol IDasherize
  nil
  (-dasherize [_] nil)
  #+clj clojure.lang.Keyword
  #+cljs cljs.core/Keyword
  (-dasherize [obj]
    (keyword (-dasherize (name obj))))
  #+clj clojure.lang.Symbol
  #+cljs cljs.core/Symbol
  (-dasherize [obj]
    (symbol (-dasherize (str obj))))
  #+clj java.lang.String
  #+cljs string
  (-dasherize [obj]
    (replace obj #"_" "-")))

(defn dasherize
  "Replaces all underscores in obj with dashes.

  Examples:

    (dasherize \"puni_puni\")
    ;=> \"puni-puni\""
  [obj] (-dasherize obj))


;; DEMODULIZE

(defprotocol IDemodulize
  (-demodulize [object] "Demodulize an object."))

(extend-protocol IDemodulize
  nil
  (-demodulize [_] nil)
  #+clj clojure.lang.Keyword
  #+cljs cljs.core/Keyword
  (-demodulize [obj]
    (keyword (-demodulize (name obj))))
  #+clj clojure.lang.Symbol
  #+cljs cljs.core/Symbol
  (-demodulize [obj]
    (symbol (-demodulize (str obj))))
  #+clj java.lang.String
  #+cljs string
  (-demodulize [obj]
    (replace obj #"^.*(::|\.)" "")))

(defn demodulize
  "Removes the module part from obj.

  Examples:

    (demodulize \"inflections.MyRecord\")
    ;=> \"MyRecord\"

    (demodulize \"ActiveRecord::CoreExtensions::String::Inflections\")
    ;=> \"Inflections\"

    (demodulize \"Inflections\")
    ;=> \"Inflections\""
  [obj] (-demodulize obj))

;; HYPHENATE

(defprotocol IHyphenate
  (-hyphenate [object] "Hyphenate an object."))

(extend-protocol IHyphenate
  nil
  (-hyphenate [_] nil)
  #+clj clojure.lang.Keyword
  #+cljs cljs.core/Keyword
  (-hyphenate [obj]
    (keyword (-hyphenate (name obj))))
  #+clj clojure.lang.Symbol
  #+cljs cljs.core/Symbol
  (-hyphenate [obj]
    (symbol (-hyphenate (str obj))))
  #+clj java.lang.String
  #+cljs string
  (-hyphenate [obj]
    (-> (replace obj #"::" "/")
        (replace #"([A-Z]+)([A-Z][a-z])" "$1-$2")
        (replace #"([a-z\d])([A-Z])" "$1-$2")
        (replace #"\s+" "-")
        (replace #"_" "-")
        (lower-case))))

(defn hyphenate
  "Hyphenate obj, which is the same as threading obj through the str,
  underscore and dasherize fns.

  Examples:

    (hyphenate 'Continent)
    ; => \"continent\"

    (hyphenate \"CountryFlag\")
    ; => \"country-flag\""
  [obj] (-hyphenate obj))

;; ORDINALIZE

(defprotocol IOrdinalize
  (-ordinalize [object] "Ordinalize an object."))

(extend-protocol IOrdinalize
  nil
  (-ordinalize [_] nil)
  #+clj clojure.lang.Keyword
  #+cljs cljs.core/Keyword
  (-ordinalize [obj]
    (keyword (-ordinalize (name obj))))
  #+clj clojure.lang.Symbol
  #+cljs cljs.core/Symbol
  (-ordinalize [obj]
    (symbol (-ordinalize (str obj))))
  #+clj java.lang.Number
  #+cljs number
  (-ordinalize [obj]
    (-ordinalize (str obj)))
  #+clj java.lang.String
  #+cljs string
  (-ordinalize [obj]
    (if-let [number (parse-integer obj)]
      (if (contains? (set (range 11 14)) (mod number 100))
        (str number "th")
        (let [modulus (mod number 10)]
          (cond
           (= modulus 1) (str number "st")
           (= modulus 2) (str number "nd")
           (= modulus 3) (str number "rd")
           :else (str number "th")))))))

(defn ordinalize
  "Turns obj into an ordinal string used to denote the position in an
  ordered sequence such as 1st, 2nd, 3rd, 4th, etc.

  Examples:

    (ordinalize \"1\")
    ;=> \"1st\"

    (ordinalize \"23\")
    ;=> \"23rd\""
  [obj] (-ordinalize obj))

;; PARAMETERIZE

(defprotocol IParameterize
  (-parameterize [object sep] "Parameterize an object."))

(extend-protocol IParameterize
  nil
  (-parameterize [_ _] nil)
  #+clj clojure.lang.Keyword
  #+cljs cljs.core/Keyword
  (-parameterize [obj sep]
    (keyword (-parameterize (name obj) sep)))
  #+clj clojure.lang.Symbol
  #+cljs cljs.core/Symbol
  (-parameterize [obj sep]
    (symbol (-parameterize (str obj) sep)))
  #+clj java.lang.String
  #+cljs string
  (-parameterize [obj sep]
    (let [sep (or sep "-")]
      (-> obj
          #+clj (replace #"(?i)[^a-z0-9]+" sep)
          #+cljs (replace #"[^A-Za-z0-9]+" sep)
          (replace #"\++" sep)
          (replace (re-pattern (str sep "{2,}")) sep)
          (replace (re-pattern (str "(?i)(^" sep ")|(" sep "$)")) "")
          lower-case))))

(defn parameterize
  "Replaces special characters in obj with the default separator
  \"-\". so that it may be used as part of a pretty URL.

  Examples:

    (parameterize \"Donald E. Knuth\")
    ; => \"donald-e-knuth\"

    (parameterize \"Donald E. Knuth\" \"_\")
    ; => \"donald_e_knuth\""
  [obj & [separator]] (-parameterize obj separator))

(defn pluralize
  "Attempts to pluralize the word unless count is 1. If plural is
  supplied, it will use that when count is > 1, otherwise it will use
  the inflector to determine the plural form."
  [count singular & [plural]]
  (str count " " (if (= 1 count) singular (or plural (inflections.core/plural singular)))))

;; UNDERSCORE

(defprotocol IUnderscore
  (-underscore [object] "Underscore an object."))

(extend-protocol IUnderscore
  nil
  (-underscore [_] nil)
  #+clj clojure.lang.Keyword
  #+cljs cljs.core/Keyword
  (-underscore [obj]
    (keyword (-underscore (name obj))))
  #+clj clojure.lang.Symbol
  #+cljs cljs.core/Symbol
  (-underscore [obj]
    (symbol (-underscore (str obj))))
  #+clj java.lang.String
  #+cljs string
  (-underscore [obj]
    (-> obj
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
  [obj] (-underscore obj))

;; FOREIGN KEY

(defprotocol IForeignKey
  (-foreign-key [object sep] "Demodulize an object."))

(extend-protocol IForeignKey
  nil
  (-foreign-key [_ _] nil)
  #+clj clojure.lang.Keyword
  #+cljs cljs.core/Keyword
  (-foreign-key [obj sep]
    (keyword (-foreign-key (name obj) sep)))
  #+clj clojure.lang.Symbol
  #+cljs cljs.core/Symbol
  (-foreign-key [obj sep]
    (symbol (-foreign-key (str obj) sep)))
  #+clj java.lang.String
  #+cljs string
  (-foreign-key [obj sep]
    (if-not (blank? obj)
      (str (underscore (hyphenate (singular (demodulize obj))))
           (or sep "_") "id"))))

(defn foreign-key
  "Converts obj into a foreign key. The default separator \"_\" is
  placed between the name and \"id\".


  Examples:

    (foreign-key \"Message\")
    ;=> \"message_id\"

    (foreign-key \"Message\" false)
    ;=> \"messageid\"

    (foreign-key \"Admin::Post\")
    ;=> \"post_id\""
  [obj & [separator]] (-foreign-key obj separator))

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
