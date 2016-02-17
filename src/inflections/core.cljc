(ns inflections.core
  (:refer-clojure :exclude [replace])
  (:require [clojure.string :refer [blank? lower-case upper-case replace split join]]
            [clojure.walk :refer [keywordize-keys]]
            [no.en.core :refer [parse-integer]]))

(defn coerce
  "Coerce the string `s` to the type of `obj`."
  [obj s]
  (cond
    (keyword? obj)
    (keyword s)
    (symbol? obj)
    (symbol s)
    :else s))

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
  (when (and rule word)
    (let [pattern (:pattern rule)
          replacement (:replacement rule)]
      (if (re-find pattern word)
        (replace word pattern replacement)))))

(defn resolve-rules [rules word]
  (first (keep #(resolve-rule % word) rules)))

(defn reset-rules!
  "Resets the list of plural rules."
  [rules] (reset! rules []))

(defn str-name
  "Same as `clojure.core/name`, but keeps the namespace for keywords
  and symbols."
  [x]
  (cond
    (nil? x)
    x
    (string? x)
    x
    (or (keyword? x)
        (symbol? x))
    (if-let [ns (namespace x)]
      (str ns "/" (name x))
      (name x))))

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

(defn acronym
  "Returns the the acronym for `s` if it is one, otherwise nil."
  [s]
  (when s
    (some->> (str-name s)
             (lower-case)
             (get @*acronyms*)
             (coerce s))))

(defn add-acronym!
  "Adds `word` to the set of `*acronyms*`."
  [word] (swap! *acronyms* assoc (lower-case (str-name word)) (str-name word)))

(defn delete-acronym!
  "Delete `word` from the set of `*acronyms*`."
  [word] (swap! *acronyms* dissoc (lower-case (str-name word))))

(defn countable?
  "Returns true if `s` is countable, otherwise false."
  [s]
  (when s (not (contains? @*uncountable-words* (lower-case (str-name s))))))

(defn uncountable?
  "Returns true if `x` is uncountable, otherwise false."
  [x]
  (when x (not (countable? x))))

(defn add-uncountable!
  "Adds `word` to the set of `*uncountable-words*`."
  [word] (swap! *uncountable-words* conj (lower-case (str-name word))))

(defn delete-uncountable!
  "Delete `word` from the set of `*uncountable-words*`."
  [word] (swap! *uncountable-words* disj (lower-case (str-name word))))

;; PLURAL

(def ^{:dynamic true} *plural-rules*
  (atom []))

(defn plural
  "Returns the plural of s."
  [s]
  (let [s (str-name s)]
    (if (or (blank? s)
            (uncountable? s))
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

(defn singular
  "Returns the singular of s."
  [s]
  (let [s' (str-name s)]
    (if-not (uncountable? s')
      (coerce s (or (resolve-rules (rseq @*singular-rules*) s') s'))
      s)))

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

(defn irregular?
  "Returns true if `word` is an irregular word, otherwise false."
  [word]
  (when word
    (contains? @*irregular-words* (lower-case (str-name word)))))

(defn add-irregular!
  "Add `singular` and `plural` to the set of `*irregular-words*`."
  [singular plural]
  (let [singular (lower-case (str-name singular))
        plural (lower-case (str-name plural))]
    (delete-uncountable! singular)
    (delete-uncountable! plural)
    (singular! (re-pattern (str "^" plural "$")) singular)
    (plural! (re-pattern (str "^" singular "$")) plural)
    (swap! *irregular-words* conj singular)
    (swap! *irregular-words* conj plural)))

(defn delete-irregular!
  "Delete `singular` and `plural` from the set of *irregular-words*."
  [singular plural]
  (let [singular (lower-case (str-name singular))
        plural (lower-case (str-name plural))]
    (swap! *irregular-words* disj singular)
    (swap! *irregular-words* disj plural)))

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

(defn camel-case
  "Convert `word` to camel case. By default, camel-case converts to
  UpperCamelCase. If the argument to camel-case is set to :lower then
  camel-case produces lowerCamelCase. The camel-case fn will also
  convert \"/\" to \"::\" which is useful for converting paths to
  namespaces.

  Examples:

    (camel-case \"active_record\")
    ;=> \"ActiveRecord\"

    (camel-case \"active_record\" :lower)
    ;=> \"activeRecord\"

    (camel-case \"active_record/errors\")
    ;=> \"ActiveRecord::Errors\"

    (camel-case \"active_record/errors\" :lower)
    ;=> \"activeRecord::Errors\""
  [word & [mode]]
  (when word
    (->> (let [word (str-name word)]
           (cond
             (= mode :lower) (camel-case word lower-case)
             (= mode :upper) (camel-case word upper-case)
             (fn? mode) (str (mode (str (first word)))
                             (apply str (rest (camel-case word nil))))
             :else (-> (replace word #"/(.?)" #(str "::" (upper-case (nth % 1))))
                       (replace #"(^|_|-)(.)"
                                #?(:clj
                                   #(str (if (#{\_ \-} (nth % 1))
                                           (nth % 1))
                                         (upper-case (nth % 2)))
                                   :cljs
                                   #(let [[_ _ letter-to-uppercase] %]
                                      (upper-case letter-to-uppercase)))))))
         (coerce word))))

(defn capitalize
  "Convert the first letter in `word` to upper case.

  Examples:

    (capitalize \"hello\")
    ;=> \"Hello\"

    (capitalize \"HELLO\")
    ;=> \"Hello\"

    (capitalize \"abc123\")
    ;=> \"Abc123\""
  [word]
  (when word
    (->> (if-let [acronym (acronym word)]
           acronym
           (let [word' (str-name word)]
             (->> (str (upper-case (str (first word')))
                       (when (next word') (lower-case (subs word' 1))))
                  (coerce word)))))))

(defn titleize
  "Convert `s` into a title."
  [s]
  (when s
    (->> (join " " (map capitalize (split (str-name s) #"[-_./ ]"))))))

(defn dasherize
  "Replaces all underscores in `s` with dashes.

  Examples:

    (dasherize \"puni_puni\")
    ;=> \"puni-puni\""
  [s]
  (when s
    (->> (replace (str-name s) #"_" "-")
         (coerce s))))

(defn demodulize
  "Removes the module part from `x`.

  Examples:

    (demodulize \"inflections.MyRecord\")
    ;=> \"MyRecord\"

    (demodulize \"ActiveRecord::CoreExtensions::String::Inflections\")
    ;=> \"Inflections\"

    (demodulize \"Inflections\")
    ;=> \"Inflections\""
  [x]
  (when x
    (->> (replace (str-name x) #"^.*(::|\.)" "")
         (coerce x))))

(defn hyphenate
  "Hyphenate x, which is the same as threading `x` through the str,
  underscore and dasherize fns.

  Examples:

    (hyphenate 'Continent)
    ; => \"continent\"

    (hyphenate \"CountryFlag\")
    ; => \"country-flag\""
  [x]
  (when x
    (->> (-> (replace (str-name x) #"::" "/")
             (replace #"([A-Z]+)([A-Z][a-z])" "$1-$2")
             (replace #"([a-z\d])([A-Z])" "$1-$2")
             (replace #"\s+" "-")
             (replace #"_" "-")
             (lower-case))
         (coerce x))))

(defn ordinalize
  "Turns `x` into an ordinal string used to denote the position in an
  ordered sequence such as 1st, 2nd, 3rd, 4th, etc.

  Examples:

    (ordinalize \"1\")
    ;=> \"1st\"

    (ordinalize \"23\")
    ;=> \"23rd\""
  [x]
  (if-let [number (parse-integer x)]
    (if (contains? (set (range 11 14)) (mod number 100))
      (str number "th")
      (let [modulus (mod number 10)]
        (cond
          (= modulus 1) (str number "st")
          (= modulus 2) (str number "nd")
          (= modulus 3) (str number "rd")
          :else (str number "th"))))))

(defn parameterize
  "Replaces special characters in `x` with the default separator
  \"-\". so that it may be used as part of a pretty URL.

  Examples:

    (parameterize \"Donald E. Knuth\")
    ; => \"donald-e-knuth\"

    (parameterize \"Donald E. Knuth\" \"_\")
    ; => \"donald_e_knuth\""
  [x & [sep]]
  (when x
    (let [sep (or sep "-")]
      (-> (str-name x)
          #?(:clj (replace #"(?i)[^a-z0-9]+" sep)
             :cljs (replace #"[^A-Za-z0-9]+" sep))
          (replace #"\++" sep)
          (replace (re-pattern (str sep "{2,}")) sep)
          (replace (re-pattern (str "(?i)(^" sep ")|(" sep "$)")) "")
          lower-case))))

(defn pluralize
  "Attempts to pluralize the word unless count is 1. If plural is
  supplied, it will use that when count is > 1, otherwise it will use
  the inflector to determine the plural form."
  [count singular & [plural]]
  (str count " " (if (= 1 count) singular (or plural (inflections.core/plural singular)))))

(defn underscore
  "The reverse of camel-case. Makes an underscored, lowercase form from
  the expression in the string. Changes \"::\" to \"/\" to convert
  namespaces to paths.

  Examples:

    (underscore \"ActiveRecord\")
    ;=> \"active_record\"

    (underscore \"ActiveRecord::Errors\")
    ;=> \"active_record/errors\""
  [x]
  (when x
    (->> (-> (replace (str-name x) #"::" "/")
             (replace #"([A-Z\d]+)([A-Z][a-z])" "$1_$2")
             (replace #"([a-z\d])([A-Z])" "$1_$2")
             (replace #"-" "_")
             lower-case)
         (coerce x))))

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
  [x & [sep]]
  (let [x' (str-name x)]
    (when-not (blank? x')
      (->> (str (underscore (hyphenate (singular (demodulize x'))))
                (or sep "_") "id")
           (coerce x)))))

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
  [m & [mode]]
  (-> (transform-keys m #(camel-case %1 mode))
      (keywordize-keys)))

(defn hyphenate-keys
  "Recursively apply hyphenate on all keys of m."
  [m] (transform-keys m hyphenate))

(defn hyphenate-values
  "Recursively apply hyphenate on all values of m."
  [m] (transform-values m hyphenate))

(defn stringify-keys
  "Recursively transform all keys of m into strings."
  [m] (transform-keys m #(if (keyword? %) (str-name %) (str %))))

(defn stringify-values
  "Recursively transform all values of m into strings."
  [m] (transform-values m #(if (keyword? %) (str-name %) (str %))))

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
