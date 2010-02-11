(ns inflections.helper
  (:refer-clojure :exclude (replace))
  (:use [clojure.contrib.str-utils2 :only (lower-case replace upper-case trim)]
        clojure.contrib.seq-utils
        inflections.helper))

(defn assert-even-args [args]
  (if-not (even? (count args))
    (throw (IllegalArgumentException. "Expected even number of arguments."))))

(defn normalize-word [word]
  (lower-case (trim (if (symbol? word) (name word) word))))

(defn parse-integer [string]
  (try (Integer/parseInt (trim string))
       (catch NumberFormatException exception nil)))
