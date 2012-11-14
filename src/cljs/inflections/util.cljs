(ns inflections.util
  (:require [clojure.string :refer [blank? split]]))

(defn parse-double
  "Parse `s` as a double number."
  [s] (let [n (js/parseFloat (str s))]
        (if-not (js/isNaN n) n)))

(defn parse-float
  "Parse `s` as a floating-point number."
  [s] (let [n (js/parseFloat (str s))]
        (if-not (js/isNaN n) n)))

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