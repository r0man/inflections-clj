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

(extend-type Object
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

(extend-type clojure.lang.Keyword
  ITransformation
  (camelize [k mode]
    (keyword (camelize (name k) mode)))
  (capitalize [k]
    (keyword (capitalize (name k))))
  (dasherize [k]
    (keyword (dasherize (name k))))
  (demodulize [k]
    (keyword (demodulize (name k))))
  (foreign-key [k sep]
    (keyword (foreign-key (name k) sep)))
  (hyphenize [k]
    (keyword (hyphenize (name k))))
  (ordinalize [k]
    (keyword (ordinalize (name k))))
  (parameterize [k sep]
    (keyword (parameterize (name k) sep)))
  (underscore [k]
    (keyword (underscore (name k)))))

(extend-type clojure.lang.Symbol
  ITransformation
  (camelize [s mode]
    (symbol (camelize (name s) mode)))
  (capitalize [s]
    (symbol (capitalize (name s))))
  (dasherize [s]
    (symbol (dasherize (name s))))
  (demodulize [s]
    (symbol (demodulize (name s))))
  (foreign-key [s sep]
    (symbol (foreign-key (name s) sep)))
  (hyphenize [s]
    (symbol (hyphenize (name s))))
  (ordinalize [s]
    (symbol (ordinalize (name s))))
  (parameterize [s sep]
    (symbol (parameterize (name s sep) sep)))
  (underscore [s]
    (symbol (underscore (name s)))))

(extend-type java.lang.String
  ITransformation
  (camelize [s mode]
    (str/camelize s mode))
  (capitalize [s]
    (str/capitalize s))
  (dasherize [s]
    (str/dasherize s))
  (demodulize [s]
    (str/demodulize s))
  (foreign-key [s sep]
    (str/foreign-key s sep))
  (hyphenize [s]
    (str/hyphenize s))
  (ordinalize [s]
    (str/ordinalize s))
  (parameterize [s sep]
    (str/parameterize s sep))
  (underscore [s]
    (str/underscore s)))

(extend-type clojure.lang.IPersistentMap
  ITransformation
  (camelize [m mode]
    (transform-keys m #(camelize % mode)))
  (capitalize [m]
    (transform-keys m capitalize))
  (dasherize [m]
    (transform-keys m dasherize))
  (demodulize [m]
    (transform-keys m demodulize))
  (foreign-key [m sep]
    (transform-keys m #(foreign-key % sep)))
  (hyphenize [m]
    (transform-keys m hyphenize))
  (ordinalize [m]
    (transform-keys m ordinalize))
  (parameterize [m sep]
    (transform-keys m #(parameterize % sep)))
  (underscore [m]
    (transform-keys m underscore)))
