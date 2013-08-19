(ns inflections.string
  (:refer-clojure :exclude [replace])
  (:require [clojure.string :refer [blank? lower-case join replace upper-case split]]
            [inflections.singular :refer [singular]]
            [inflections.util :as util]))

(defn camelize [s mode]
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

(defn capitalize [s]
  (str (upper-case (str (first s)))
       (lower-case (apply str (rest s)))))

(defn dasherize [s]
  (replace s #"_" "-"))

(defn demodulize [s]
  (replace s #"^.*(::|\.)" ""))

(defn underscore [s]
  (replace s #"-" "_"))

(defn hyphenize [s]
  (-> (replace s #"::" "/")
      (replace #"([A-Z]+)([A-Z][a-z])" "$1-$2")
      (replace #"([a-z\d])([A-Z])" "$1-$2")
      (replace #"\s+" "-")
      (replace #"_" "-")
      (lower-case)))

(defn ordinalize [s]
  (if-let [number (util/parse-integer s)]
    (if (contains? (set (range 11 14)) (mod number 100))
      (str number "th")
      (let [modulus (mod number 10)]
        (cond
         (= modulus 1) (str number "st")
         (= modulus 2) (str number "nd")
         (= modulus 3) (str number "rd")
         :else (str number "th"))))))

(defn parameterize [s sep]
  (let [sep (or sep "-")]
    (-> s
        (replace #"(?i)[^a-z0-9]+" sep)
        (replace #"\++" sep)
        (replace (re-pattern (str sep "{2,}")) sep)
        (replace (re-pattern (str "(?i)(^" sep ")|(" sep "$)")) "")
        lower-case)))

(defn foreign-key [s sep]
  (if-not (blank? s)
    (str (underscore (hyphenize (singular (demodulize s))))
         (or sep "_") "id")))
