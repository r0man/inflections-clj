(ns inflections.transform
  (:refer-clojure :exclude [replace])
  (:use [clojure.string :only [blank? lower-case replace upper-case]]))

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

(extend-type number
  ITransformation
  (ordinalize [n]
    (ordinalize (str n))))

(extend-type string
  ITransformation

  ;; TODO: Fix me!
  (camelize [s mode]
    (cond
     (blank? s) s
     (= mode :lower) (camelize s lower-case)
     (= mode :upper) (camelize s upper-case)
     (fn? mode) (str (mode (str (first s)))
                     (apply str (rest (camelize s nil))))
     :else (-> s
               (replace #"/(.?)" #(str "::" (upper-case (nth % 1))))
               (replace #"(?:^|_|-)(.)" #(upper-case (nth % 1))))))

  (capitalize [s]
    (let [result (str (upper-case (str (first s)))
                      (lower-case (apply str (rest s))))]
      (cond (keyword? s) (keyword result)
            (symbol? s) (symbol result)
            :else result)))

  (dasherize [s]
    (replace s #"_" "-"))

  (demodulize [s]
    (replace s #"^.*(::|\.)" ""))

  (foreign-key [s sep]
    (if-not (blank? s)
      (str (underscore (demodulize s)) (or sep "_") "id")))

  (hyphenize [s]
    (-> (underscore s)
        (dasherize)
        (replace #"\s+" "-")))

  (ordinalize [s]
    (let [number (js/parseInt s)]
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
          (replace #"[^A-Za-z0-9]+" sep)
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
