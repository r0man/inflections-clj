(ns inflections.util
  (:refer-clojure :exclude [replace])
  (:require [clojure.string :refer [blank? replace split upper-case]]))

(defn- apply-unit [number unit]
  (if (string? unit)
    (case (upper-case unit)
      (case unit
        "M" (* number 1000000)
        "B" (* number 1000000000)))
    number))

(defn- parse-number [s parse-fn]
  (if-let [matches (re-matches #"\s*([-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?)(M|B)?.*" (str s))]
    (let [number (parse-fn (nth matches 1))
          unit (nth matches 3)]
      (if-not (js/isNaN number)
        (apply-unit number unit)))))

(defn parse-float
  "Parse `s` as a float number."
  [s] (parse-number s js/parseFloat))

(defn parse-integer
  "Parse `s` as a integer number."
  [s] (parse-number s js/parseInt))

(defn parse-double
  "Parse `s` as a double number."
  [s] (parse-float s))

(defn parse-long
  "Parse `s` as a long number."
  [s] (parse-integer s))

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
