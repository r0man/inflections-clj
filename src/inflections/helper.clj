(ns inflections.helper
  (:refer-clojure :exclude (replace))
  (:use [clojure.contrib.str-utils2 :only (lower-case replace upper-case trim)]
        clojure.contrib.seq-utils
        inflections.helper))

(defn normalize-word [word]
  (lower-case (trim (if (symbol? word) (name word) word))))

(defn capitalize
  "Returns a string with the first character of the given word
  converted to uppercase and the remainder to lowercase."
  [word]
  (str
   (upper-case (str (first word)))
   (lower-case (apply str (rest word)))))

(defn dasherize
  "Replaces underscores with dashes in the string."
  [word]
  (replace word #"_" "-"))

(defn parse-integer [string]
  (try (Integer/parseInt (trim string))
       (catch NumberFormatException exception nil)))

(defn ordinalize
  "Turns a number into an ordinal string used to denote the position
  in an ordered sequence such as 1st, 2nd, 3rd, 4th."
  [number]
  (if-let [number (parse-integer number)]
    (if (includes? (range 11 14) (mod number 100))
      (str number "th")
      (let [modulus (mod number 10)]
        (cond
         (= modulus 1) (str number "st")
         (= modulus 2) (str number "nd")
         (= modulus 3) (str number "rd")
         :else (str number "th"))))))

(defn underscore
  "Makes an underscored, lowercased version from the given word."
  [word]
  (replace word #"[-\s]+" "_"))




