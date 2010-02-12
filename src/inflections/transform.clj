(ns inflections.transform
  (:refer-clojure :exclude (replace))
  (:use [clojure.contrib.str-utils2 :only (lower-case replace upper-case trim)]
        clojure.contrib.seq-utils
        inflections.helper))

(defn capitalize
  "Returns a string with the first character of the word converted to
  uppercase and the remaining to lowercase.\n
  Examples: (capitalize \"hello\") => \"Hello\"
            (capitalize \"HELLO\") => \"Hello\"
            (capitalize \"abc123\") => \"abc123\""
  [word]
  (str
   (upper-case (str (first word)))
   (lower-case (apply str (rest word)))))

(defn dasherize
  "Replaces all underscores in the word with dashes.\n
  Example: (dasherize \"puni_puni\") => \"puni-puni\""
  [word]
  (replace word #"_" "-"))

(defn demodulize
  "Removes the module part from the expression in the string. \n
  Examples: (demodulize \"ActiveRecord::CoreExtensions::String::Inflections\") => \"Inflections\"
            (demodulize \"Inflections\") => \"Inflections\""
  [word] (replace word #"^.*::" ""))

(defn ordinalize
  "Turns a number into an ordinal string used to denote the position
  in an ordered sequence such as 1st, 2nd, 3rd, 4th, etc.\n
  Examples: (ordinalize \"1\") => \"1st\"
            (ordinalize \"23\") => \"23rd\""
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
  "The reverse of camelize. Makes an underscored, lowercase form from
  the expression in the string. Changes \"::\" to \"/\" to convert
  namespaces to paths.\n
  Examples: (dasherize \"ActiveRecord\") => \"active_record\"
           (dasherize \"ActiveRecord::Errors\") => \"active_record/errors\""
  [word]
  (-> word
      (replace #"::" "/")
      (replace #"([A-Z]+)([A-Z][a-z])" "$1_$2")
      (replace #"([a-z\d])([A-Z])" "$1_$2")
      (replace "-" "_")
      (lower-case)))


