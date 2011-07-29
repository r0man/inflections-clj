(ns inflections.transform
  (:refer-clojure :exclude [replace])
  (:use [clojure.string :only (blank? lower-case replace trim upper-case)]
        inflections.helper))

(defprotocol Inflection
  (camelize [object mode] "Camelize object.")
  (capitalize [object] "Capitalize object.")
  (dasherize [object] "Dasherize object.")
  (demodulize [object] "Demodulize object.")
  (foreign-key [object sep] "Make object a foreign key.")
  (hyphenize [object] "Hyphenize object.")
  (ordinalize [object] "Ordinalize object.")
  (parameterize [object sep] "Parameterize object.")
  (underscore [object] "Underscore object.")
  (underscore-keys [object] "Underscore all keys in object."))

(extend-type nil
  Inflection
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

(extend-type clojure.lang.Keyword
  Inflection
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
    (keyword (parameterize (name k sep) sep)))
  (underscore [k]
    (keyword (underscore (name k)))))

(extend-type java.lang.Integer
  Inflection
  (ordinalize [n]
    (ordinalize (str n))))

(extend-type clojure.lang.Symbol
  Inflection
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
  Inflection

  (camelize [s mode]
    (cond
     (= mode :lower) (camelize s lower-case)
     (= mode :upper) (camelize s upper-case)
     (fn? mode) (str (mode (str (first s)))
                     (apply str (rest (camelize s nil))))
     :else (-> s
               (replace #"/(.?)" #(str "::" (upper-case (nth % 1))))
               (replace #"(?:^|_|-)(.)" #(upper-case (nth % 1))))))

  (capitalize [s]
    (str (upper-case (str (first s)))
         (lower-case (apply str (rest s)))))

  (dasherize [s]
    (replace s #"_" "-"))

  (demodulize [s]
    (replace s #"^.*(::|\.)" ""))

  (foreign-key [s sep]
    (if-not (blank? s)
      (str (underscore (demodulize s)) (or sep "_") "id")))

  (hyphenize [s]
    (-> s underscore dasherize))

  (ordinalize [s]
    (if-let [number (parse-integer s)]
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
    (-> s
        (replace #"::" "/")
        (replace #"([A-Z]+)([A-Z][a-z])" "$1_$2")
        (replace #"([a-z\d])([A-Z])" "$1_$2")
        (replace #"-" "_")
        (lower-case))))
