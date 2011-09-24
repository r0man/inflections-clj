(ns #^{:author "Roman Scherer" :doc "Rails-like inflections for Clojure."}
  inflections.core
  (:require [inflections.transform :as t]
            [inflections.irregular :as i]
            [inflections.uncountable :as u]
            [inflections.plural :as p]
            [inflections.singular :as s])
  (:use [clojure.walk :only (postwalk)]))

(defn camelize
  "Convert obj to camel case. By default, camelize converts to
  UpperCamelCase. If the argument to camelize is set to :lower then
  camelize produces lowerCamelCase. The camelize fn will also convert
  \"/\" to \"::\" which is useful for converting paths to namespaces.

  Examples:

    (camelize \"active_record\")
    ;=> \"ActiveRecord\"

    (camelize \"active_record\" :lower)
    ;=> \"activeRecord\"

    (camelize \"active_record/errors\")
    ;=> \"ActiveRecord::Errors\"

    (camelize \"active_record/errors\" :lower)
    ;=> \"activeRecord::Errors\""
  [obj & [mode]] (t/camelize obj mode))

(defn capitalize
  "Convert the first letter in obj to upper case.

  Examples:

    (capitalize \"hello\")
    ;=> \"Hello\"

    (capitalize \"HELLO\")
    ;=> \"Hello\"

    (capitalize \"abc123\")
    ;=> \"Abc123\""
  [obj] (t/capitalize obj))

(defn dasherize
  "Replaces all underscores in obj with dashes.

  Examples:

    (dasherize \"puni_puni\")
    ;=> \"puni-puni\""
  [obj] (t/dasherize obj))

(defn demodulize
  "Removes the module part from obj.

  Examples:

    (demodulize \"inflections.MyRecord\")
    ;=> \"MyRecord\"

    (demodulize \"ActiveRecord::CoreExtensions::String::Inflections\")
    ;=> \"Inflections\"

    (demodulize \"Inflections\")
    ;=> \"Inflections\""
  [obj] (t/demodulize obj))

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
  [obj & [separator]] (t/foreign-key obj separator))

(defn hyphenize
  "Hyphenize obj, which is the same as threading obj through the str,
  underscore and dasherize fns.

  Examples:

    (hyphenize 'Continent)
    ; => \"continent\"

    (hyphenize \"CountryFlag\")
    ; => \"country-flag\""
  [obj] (t/hyphenize obj))

(defn irregular?
  "Returns true if obj is a irregular word, otherwise false

  Examples:

    (irregular? \"child\")
    ;=> true

    (irregular? \"word\")
    ;=> false"
  [obj] (i/irregular? obj))

(defn ordinalize
  "Turns obj into an ordinal string used to denote the position in an
  ordered sequence such as 1st, 2nd, 3rd, 4th, etc.

  Examples:

    (ordinalize \"1\")
    ;=> \"1st\"

    (ordinalize \"23\")
    ;=> \"23rd\""
  [obj] (t/ordinalize obj))

(defn parameterize
  "Replaces special characters in obj with the default separator
  \"-\". so that it may be used as part of a pretty URL.

  Examples:

    (parameterize \"Donald E. Knuth\")
    ; => \"donald-e-knuth\"

    (parameterize \"Donald E. Knuth\" \"_\")
    ; => \"donald_e_knuth\""
  [obj & [separator]] (t/parameterize obj separator))

(defn plural
  "Returns the plural of obj.

  Example:

    (plural \"virus\")
    ; => \"virii\""
  [obj] (p/plural obj))

(defn singular
  "Returns the singular of obj.

  Example:

    (singular \"mice\")
    ;=> \"mouse\""
  [obj] (s/singular obj))

(defn transform-keys
  "Recursively transform all map keys of m by applying f on them."
  [m f]
  (reduce
   (fn [memo key]
     (let [value (get m key)]
       (-> memo
           (dissoc key)
           (assoc (f key) (if (map? value) (transform-keys value f) value)))))
   m (keys m)))

(defn transform-values
  "Recursively transform all map values of m by applying f on them."
  [m f]
  (reduce
   (fn [memo key]
     (let [value (get m key)]
       (assoc memo key (if (map? value) (transform-values value f) (f value)))))
   m (keys m)))

(defn underscore
  "The reverse of camelize. Makes an underscored, lowercase form from
  the expression in the string. Changes \"::\" to \"/\" to convert
  namespaces to paths.

  Examples:

    (underscore \"ActiveRecord\")
    ;=> \"active_record\"

    (underscore \"ActiveRecord::Errors\")
    ;=> \"active_record/errors\""
  [obj] (t/underscore obj))

(defn hyphenize-keys
  "Recursively apply hyphenize on all keys of m."
  [m] (transform-keys m hyphenize))

(defn stringify-keys
  "Recursively transform all keys of m into strings."
  [m] (transform-keys m #(if (keyword? %) (name %) (str %))))

(defn stringify-values
  "Recursively transform all values of m into strings."
  [m] (transform-values m #(if (keyword? %) (name %) (str %))))

(defn underscore-keys
  "Recursively apply underscore on all keys of m."
  [m] (transform-keys m underscore))

(defn uncountable?
  "Returns true if obj is a uncountable word, otherwise false.

  Examples:

    (uncountable? \"alcohol\")
    ;=> true

    (uncountable? \"word\")
    ;=> false"
  [obj] (u/uncountable? obj))

(defn init-inflections []
  (p/init-plural-rules)
  (s/init-singular-rules)
  (u/init-uncountable-words)
  (i/init-irregular-words))

(init-inflections)

(def singularize singular)
(def pluralize plural)
