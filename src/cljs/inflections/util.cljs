(ns inflections.util
  (:refer-clojure :exclude [replace])
  (:require [clojure.string :refer [blank? replace split]]))

(defn parse-float [s]
  (if-let [matches (re-matches #"\s*([-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?)(M|B)?\s*" (str s))]
    (let [number (js/parseFloat (nth matches 1))]
      (if-let [unit (nth matches 3)]
        (case unit
          "M" (* number (.pow js/Math 10 6))
          "B" (* number (.pow js/Math 10 9)))
        number))))

(defn parse-double
  "Parse `s` as a floating-point number."
  [s] (parse-float s))

(defn parse-integer
  "Parse `s` as a integer."
  [s] (let [n (js/parseInt (str s))]
        (if-not (js/isNaN n) n)))

(defn parse-location
  "Parse `s` as a latitude/longitude location map."
  [s]
  (let [regex #"(,)|(\s+)"
        [latitude longitude]
        (->> (split (str s) regex)
             (remove #(or (blank? %1)
                          (re-matches regex (str %1))))
             (map parse-double))]
    (when (and latitude longitude)
      {:latitude latitude
       :longitude longitude})))

(defn parse-url
  "Parse `s` as a url and return a Ring compatible map."
  [s]
  (if-let [matches (re-matches #"([^:]+)://(([^:]+):([^@]+)@)?(([^:/]+)(:([0-9]+))?((/[^?]*)(\?(.*))?)?)" s)]
    {:scheme (nth matches 1)
     :user (nth matches 3)
     :password (nth matches 4)
     :server-name (nth matches 6)
     :server-port (parse-integer (nth matches 8))
     :uri (nth matches 10)
     :params (if (nth matches 12)
               (->> (split (or (nth matches 12) "") #"&")
                    (map #(split %1 #"="))
                    (mapcat #(vector (keyword (first %1)) (second %1)))
                    (apply hash-map)))
     :query-string (nth matches 12)}))

(defn parse-percent
  "Parse `s` as a percentage."
  [s] (parse-double (replace s "%" "")))
