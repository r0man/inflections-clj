(ns inflections.util
  (:refer-clojure :exclude [replace])
  (:require [clojure.string :refer [replace split]]))

(defn parse-double [s]
  (if-let [matches (re-matches #"\s*([-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?)(M|B)?\s*" (str s))]
    (let [number (Double/parseDouble (nth matches 1))]
      (if-let [unit (nth matches 3)]
        (case unit
          "M" (* number 1000000)
          "B" (* number 1000000000))
        number))))

(defn parse-float
  "Parse `s` as a float number."
  [s]
  (if-let [matches (re-matches #"\s*([-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?)(M|B)?\s*" (str s))]
    (let [number (Float/parseFloat (nth matches 1))]
      (if-let [unit (nth matches 3)]
        (case unit
          "M" (* number 1000000)
          "B" (* number 1000000000))
        number))))

(defn parse-integer
  "Parse `s` as a integer."
  [s]
  (if-let [matches (re-matches #"\s*([-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?)(M|B)?\s*" (str s))]
    (let [number (Integer/parseInt (nth matches 1))]
      (if-let [unit (nth matches 3)]
        (case unit
          "M" (* number 1000000)
          "B" (* number 1000000000))
        number))))

(defn parse-long
  "Parse `s` as a long."
  [s]
  (if-let [matches (re-matches #"\s*([-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?)(M|B)?\s*" (str s))]
    (let [number (Integer/parseInt (nth matches 1))]
      (if-let [unit (nth matches 3)]
        (case unit
          "M" (* number 1000000)
          "B" (* number 1000000000))
        number))))

(defn parse-location
  "Parse `s` as a latitude/longitude location map."
  [s] (let [[lat lon] (map parse-double (split (str s) #"(,)|(\s+)"))]
        (if (and lat lon)
          {:latitude lat :longitude lon})))

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
