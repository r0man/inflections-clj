(ns #^{:author "Roman Scherer" :doc "Rails-like inflections for Clojure."}
  inflections.core
  ;; (:require [inflections.transform :as t]
  ;;           [inflections.irregular :as i]
  ;;           [inflections.uncountable :as u]
  ;;           [inflections.plural :as p]
  ;;           [inflections.singular :as s])
  (:refer-clojure :exclude [replace])
  (:require [clojure.string :refer [lower-case upper-case replace]]
            [clojure.walk :refer [postwalk]]))

;; CAMELIZE

(defprotocol ICamelize
  (-camelize [object mode] "Camelize an object."))

(extend-protocol ICamelize
  nil
  (-camelize [_ _] nil)
  #+clj clojure.lang.Keyword
  #+cljs cljs.core/Keyword
  (-camelize [obj mode]
    (keyword (-camelize (apply str (rest (str obj))) mode)))
  #+clj clojure.lang.Symbol
  #+cljs cljs.core/Symbol
  (-camelize [obj mode]
    (symbol (-camelize (str obj) mode)))
  #+clj java.lang.String
  #+cljs string
  (-camelize [obj mode]
    (cond
     (= mode :lower) (-camelize obj lower-case)
     (= mode :upper) (-camelize obj upper-case)
     (fn? mode) (str (mode (str (first obj)))
                     (apply str (rest (-camelize obj nil))))
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
  [obj & [mode]] (-camelize obj mode))


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

;; (defn dasherize
;;   "Replaces all underscores in obj with dashes.

;;   Examples:

;;     (dasherize \"puni_puni\")
;;     ;=> \"puni-puni\""
;;   [obj] (t/dasherize obj))


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

;; (defn foreign-key
;;   "Converts obj into a foreign key. The default separator \"_\" is
;;   placed between the name and \"id\".


;;   Examples:

;;     (foreign-key \"Message\")
;;     ;=> \"message_id\"

;;     (foreign-key \"Message\" false)
;;     ;=> \"messageid\"

;;     (foreign-key \"Admin::Post\")
;;     ;=> \"post_id\""
;;   [obj & [separator]] (t/foreign-key obj separator))

;; (defn hyphenize
;;   "Hyphenize obj, which is the same as threading obj through the str,
;;   underscore and dasherize fns.

;;   Examples:

;;     (hyphenize 'Continent)
;;     ; => \"continent\"

;;     (hyphenize \"CountryFlag\")
;;     ; => \"country-flag\""
;;   [obj] (t/hyphenize obj))

;; (defn irregular?
;;   "Returns true if obj is a irregular word, otherwise false

;;   Examples:

;;     (irregular? \"child\")
;;     ;=> true

;;     (irregular? \"word\")
;;     ;=> false"
;;   [obj] (i/irregular? obj))

;; (defn ordinalize
;;   "Turns obj into an ordinal string used to denote the position in an
;;   ordered sequence such as 1st, 2nd, 3rd, 4th, etc.

;;   Examples:

;;     (ordinalize \"1\")
;;     ;=> \"1st\"

;;     (ordinalize \"23\")
;;     ;=> \"23rd\""
;;   [obj] (t/ordinalize obj))

;; (defn parameterize
;;   "Replaces special characters in obj with the default separator
;;   \"-\". so that it may be used as part of a pretty URL.

;;   Examples:

;;     (parameterize \"Donald E. Knuth\")
;;     ; => \"donald-e-knuth\"

;;     (parameterize \"Donald E. Knuth\" \"_\")
;;     ; => \"donald_e_knuth\""
;;   [obj & [separator]] (t/parameterize obj separator))

;; (defn plural
;;   "Returns the plural of obj.

;;   Example:

;;     (plural \"virus\")
;;     ; => \"virii\""
;;   [obj] (p/plural obj))

;; (defn pluralize
;;   "Attempts to pluralize the word unless count is 1. If plural is
;;   supplied, it will use that when count is > 1, otherwise it will use
;;   the inflector to determine the plural form."
;;   [count singular & [plural]]
;;   (str count " " (if (= 1 count) singular (or plural (p/plural singular)))))

;; (defn singular
;;   "Returns the singular of obj.

;;   Example:

;;     (singular \"mice\")
;;     ;=> \"mouse\""
;;   [obj] (s/singular obj))

;; (defn underscore
;;   "The reverse of camelize. Makes an underscored, lowercase form from
;;   the expression in the string. Changes \"::\" to \"/\" to convert
;;   namespaces to paths.

;;   Examples:

;;     (underscore \"ActiveRecord\")
;;     ;=> \"active_record\"

;;     (underscore \"ActiveRecord::Errors\")
;;     ;=> \"active_record/errors\""
;;   [obj] (t/underscore obj))

;; (defn uncountable?
;;   "Returns true if obj is a uncountable word, otherwise false.

;;   Examples:

;;     (uncountable? \"alcohol\")
;;     ;=> true

;;     (uncountable? \"word\")
;;     ;=> false"
;;   [obj] (u/uncountable? obj))

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

(defn camelize-keys
  "Recursively apply camelize on all keys of m."
  [m & [mode]] (transform-keys m #(camelize %1 mode)))

;; (defn hyphenize-keys
;;   "Recursively apply hyphenize on all keys of m."
;;   [m] (t/transform-keys m hyphenize))

;; (defn hyphenize-values
;;   "Recursively apply hyphenize on all values of m."
;;   [m] (t/transform-values m hyphenize))

;; (defn stringify-keys
;;   "Recursively transform all keys of m into strings."
;;   [m] (t/transform-keys m #(if (keyword? %) (name %) (str %))))

;; (defn stringify-values
;;   "Recursively transform all values of m into strings."
;;   [m] (t/transform-values m #(if (keyword? %) (name %) (str %))))

;; (defn underscore-keys
;;   "Recursively apply underscore on all keys of m."
;;   [m] (t/transform-keys m underscore))

;; (defn init-inflections []
;;   (p/init-plural-rules)
;;   (s/init-singular-rules)
;;   (i/init-irregular-words))

;; (init-inflections)
