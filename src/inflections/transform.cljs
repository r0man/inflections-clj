(ns inflections.transform
  (:refer-clojure :exclude [replace])
  (:require [clojure.string :refer [blank? lower-case join replace upper-case split]]
            [inflections.singular :refer [singular]]
            [inflections.string :as str]))

(defprotocol ITransformation
  (camelize [object mode]
    "Camelize object.")
  (capitalize [object]
    "Capitalize object.")
  (dasherize [object]
    "Dasherize object.")
  (demodulize [object]
    "Demodulize object.")
  (foreign-key [object sep]
    "Make object a foreign key.")
  (hyphenize [object]
    "Hyphenize object.")
  (ordinalize [object]
    "Ordinalize object.")
  (parameterize [object sep]
    "Parameterize object.")
  (underscore [object]
    "Underscore object."))

(defn- coerce-keyword [original result]
  (if (keyword? original)
    (keyword result)
    result))

(defn transform-keys
  "Recursively transform all map keys of m by applying f on them."
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

(extend-type nil
  ITransformation
  (camelize [_ mode]
    nil)
  (capitalize [_]
    nil)
  (dasherize [_]
    nil)
  (demodulize [_]
    nil)
  (foreign-key [_ sep]
    nil)
  (hyphenize [_]
    nil)
  (ordinalize [_]
    nil)
  (parameterize [_ sep]
    nil)
  (underscore [_]
    nil))

(extend-type default
  ITransformation
  (camelize [obj mode]
    (camelize (str obj) mode))
  (capitalize [obj]
    (capitalize (str obj)))
  (dasherize [obj]
    (dasherize (str obj)))
  (demodulize [obj]
    (demodulize (str obj)))
  (foreign-key [obj sep]
    (foreign-key (str obj) sep))
  (hyphenize [obj]
    (hyphenize (str obj)))
  (ordinalize [obj]
    (ordinalize (str obj)))
  (parameterize [obj sep]
    (parameterize (str obj) sep))
  (underscore [obj]
    (underscore (str obj))))

(extend-type cljs.core/Symbol
  ITransformation
  (camelize [s mode]
    (symbol (str/camelize (str s) mode)))
  (capitalize [s]
    (symbol (str/capitalize (str s))))
  (dasherize [s]
    (symbol (str/dasherize (str s))))
  (demodulize [s]
    (symbol (str/demodulize (str s))))
  (foreign-key [s sep]
    (symbol (str/foreign-key (str s) sep)))
  (hyphenize [s]
    (symbol (str/hyphenize (str s))))
  (ordinalize [s]
    (symbol (str/ordinalize (str s))))
  (parameterize [s sep]
    (symbol (str/parameterize (str s) sep)))
  (underscore [s]
    (symbol (str/underscore (str s)))))

(extend-type string
  ITransformation
  (camelize [s mode]
    (coerce-keyword
     s (let [s (name s)]
         (cond
          (= mode :lower) (camelize s lower-case)
          (= mode :upper) (camelize s upper-case)
          (fn? mode) (str (mode (str (first s)))
                          (apply str (rest (camelize s nil))))
          :else (-> (str s)
                    (replace #"/(.?)" #(str "::" (upper-case (nth % 1))))
                    (replace #"(^|_|-)(.)" #(let [[f r] %]
                                              (str (if-not (#{\_ \-} f)
                                                     (upper-case f))
                                                   (if r (upper-case r))))))))))
  (capitalize [s]
    (coerce-keyword s (str/capitalize s)))
  (dasherize [s]
    (coerce-keyword s (str/dasherize s)))
  (demodulize [s]
    (coerce-keyword s (str/demodulize s)))
  (foreign-key [s sep]
    (coerce-keyword s (str/foreign-key s sep)))
  (hyphenize [s]
    (coerce-keyword s (str/hyphenize s)))
  (ordinalize [s]
    (coerce-keyword s (str/ordinalize s)))
  (parameterize [s sep]
    (coerce-keyword s (str/parameterize s sep)))
  (underscore [s]
    (coerce-keyword s (str/underscore s))))

;; (extend-type clojure.lang.IPersistentMap
;;   ITransformation
;;   (camelize [m mode]
;;     (transform-keys m #(camelize % mode)))
;;   (capitalize [m]
;;     (transform-keys m capitalize))
;;   (dasherize [m]
;;     (transform-keys m dasherize))
;;   (demodulize [m]
;;     (transform-keys m demodulize))
;;   (foreign-key [m sep]
;;     (transform-keys m #(foreign-key % sep)))
;;   (hyphenize [m]
;;     (transform-keys m hyphenize))
;;   (ordinalize [m]
;;     (transform-keys m ordinalize))
;;   (parameterize [m sep]
;;     (transform-keys m #(parameterize % sep)))
;;   (underscore [m]
;;     (transform-keys m underscore)))
