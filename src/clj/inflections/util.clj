(ns inflections.util
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