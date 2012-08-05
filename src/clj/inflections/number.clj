(ns inflections.number
  (:require [clojure.string :refer [split]]))

(defn parse-double
  "Parse `s` as a double number."
  [s] (try (Double/parseDouble (str s))
           (catch NumberFormatException _ nil)))

(defn parse-float
  "Parse `s` as a float number."
  [s] (try (Float/parseFloat (str s))
           (catch NumberFormatException _ nil)))

(defn parse-integer
  "Parse `s` as a integer."
  [s] (try (Integer/parseInt (str s))
           (catch NumberFormatException _ nil)))

(defn parse-long
  "Parse `s` as a long."
  [s] (try (Long/parseLong (str s))
           (catch NumberFormatException _ nil)))

(defn parse-location
  "Parse `s` as a latitude/longitude location map."
  [s] (let [[lat lon] (map parse-double (split (str s) #"(,)|(\s+)"))]
        (if (and lat lon)
          {:latitude lat :longitude lon})))
