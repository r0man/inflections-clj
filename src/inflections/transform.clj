(ns inflections.transform
  (:refer-clojure :exclude [replace])
  (:use [clojure.string :only [blank? lower-case join replace upper-case split]]
        [inflections.singular :only [singular]]))

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
    (cond
     (= mode :lower) (camelize s lower-case)
     (= mode :upper) (camelize s upper-case)
     (fn? mode) (str (mode (str (first s)))
                     (apply str (rest (camelize s nil))))
     :else (-> s
               (replace #"/(.?)" #(str "::" (upper-case (nth % 1))))
               (replace #"(^|_|-)(.)" #(str (if (#{\_ \-} (nth % 1))
                                              (nth % 1))
                                            (upper-case (nth % 2)))))))

  (capitalize [s]
    (str (upper-case (str (first s)))
         (lower-case (apply str (rest s)))))

  (dasherize [s]
    (replace s #"_" "-"))

  (demodulize [s]
    (replace s #"^.*(::|\.)" ""))

  (foreign-key [s sep]
    (if-not (blank? s)
      (str (underscore (hyphenize (singular (demodulize s))))
           (or sep "_") "id")))

  (hyphenize [s]
    (-> (replace s #"::" "/")
        (replace #"([A-Z]+)([A-Z][a-z])" "$1-$2")
        (replace #"([a-z\d])([A-Z])" "$1-$2")
        (replace #"\s+" "-")
        (replace #"_" "-")
        (lower-case)))

  (ordinalize [s]
    (let [number (Integer/parseInt s)]
      (if (contains? (set (range 11 14)) (mod number 100))
        (str number "th")
        (let [modulus (mod number 10)]
          (cond
           (= modulus 1) (str number "st")
           (= modulus 2) (str number "nd")
           (= modulus 3) (str number "rd")
           :else (str number "th"))))))

  (parameterize [s sep]
    (let [sep (or sep "-")]
      (-> s
          (replace #"(?i)[^a-z0-9]+" sep)
          (replace #"\++" sep)
          (replace (re-pattern (str sep "{2,}")) sep)
          (replace (re-pattern (str "(?i)(^" sep ")|(" sep "$)")) "")
          lower-case)))

  (underscore [s]
    (replace s #"-" "_")))

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
