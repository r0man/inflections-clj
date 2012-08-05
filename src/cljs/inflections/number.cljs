(ns inflections.number
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
