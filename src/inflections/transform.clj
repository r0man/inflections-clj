(ns inflections.transform
  (:import java.util.regex.Pattern)
  (:use [clojure.contrib.seq-utils :only (includes?)]
        [clojure.contrib.string :only (blank? lower-case replace-by replace-re replace-str trim upper-case)]
        [clojure.walk :only (postwalk)]
        inflections.helper))

(defn normalize
  "Normalize a word."
  [arg]
  (cond
   (nil? arg) nil
   (isa? (class arg) String) arg
   :else (pr-str arg)))

(defn camelize
  "By default, camelize converts strings to UpperCamelCase. If the
 argument to camelize is set to :lower then camelize produces
 lowerCamelCase. camelize will also convert \"/\" to \"::\" which is
 useful for converting paths to namespaces.\n
 Examples: (camelize \"active_record\") => \"ActiveRecord\"
           (camelize \"active_record\") => \"activeRecord\"
           (camelize \"active_record/errors\") => \"ActiveRecord::Errors\"
           (camelize \"active_record/errors\" :lower) => \"activeRecord::Errors\""
  ([word]
     (if-let [word (normalize word)]
       (->> word
            (replace-by #"/(.?)" #(str "::" (upper-case (nth % 1))))
            (replace-by #"(?:^|_|-)(.)" #(upper-case (nth % 1))))))
  ([word mode]
     (if-let [word (normalize word)]
       (cond
        (= mode :lower) (camelize word lower-case)
        (= mode :upper) (camelize word upper-case)
        (fn? mode) (str (mode (str (first word)))
                        (apply str (rest (camelize word))))))))

(defn capitalize
  "Returns a string with the first character of the word converted to
  uppercase and the remaining to lowercase.\n
  Examples: (capitalize \"hello\") => \"Hello\"
            (capitalize \"HELLO\") => \"Hello\"
            (capitalize \"abc123\") => \"abc123\""
  [word]
  (if-let [word (normalize word)]
    (str (upper-case (str (first word)))
         (lower-case (apply str (rest word))))))

(defn dasherize
  "Replaces all underscores in the word with dashes.\n
  Example: (dasherize \"puni_puni\") => \"puni-puni\""
  [word]
  (if (keyword? word)
    (keyword (dasherize (name word)))
    (if-let [word (normalize word)]
      (replace-re #"_" "-" word))))

(defn demodulize
  "Removes the module part from the expression in the string. \n
  Examples: (demodulize \"inflections.MyRecord\") => \"MyRecord\"
            (demodulize \"ActiveRecord::CoreExtensions::String::Inflections\") => \"Inflections\"
            (demodulize \"Inflections\") => \"Inflections\""
  [word]
  (if-let [word (normalize word)]
    (replace-re #"^.*(::|\.)" "" word)))

(defn ordinalize
  "Turns a number into an ordinal string used to denote the position
  in an ordered sequence such as 1st, 2nd, 3rd, 4th, etc.\n
  Examples: (ordinalize \"1\") => \"1st\"
            (ordinalize \"23\") => \"23rd\""
  [number]
  (if-not (blank? number)
    (if-let [number (parse-integer number)]
     (if (includes? (range 11 14) (mod number 100))
       (str number "th")
       (let [modulus (mod number 10)]
         (cond
          (= modulus 1) (str number "st")
          (= modulus 2) (str number "nd")
          (= modulus 3) (str number "rd")
          :else (str number "th")))))))

(defn underscore
  "The reverse of camelize. Makes an underscored, lowercase form from
  the expression in the string. Changes \"::\" to \"/\" to convert
  namespaces to paths.\n
  Examples: (underscore \"ActiveRecord\") => \"active_record\"
            (underscore \"ActiveRecord::Errors\") => \"active_record/errors\""
  [word]
  (if (keyword? word)
    (keyword (underscore (name word)))
    (if-let [word (normalize word)]
     (->> word
          (replace-re #"::" "/")
          (replace-re #"([A-Z]+)([A-Z][a-z])" "$1_$2")
          (replace-re #"([a-z\d])([A-Z])" "$1_$2")
          (replace-re #"-" "_")
          (lower-case)))))

(defn underscore-keys
  "Recursively replaces all dashes with underscore of all keys in m."
  [m] (let [f (fn [[k v]]
                [(underscore k)
                 (if (map? v) (underscore-keys v) v)])]
        (postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn foreign-key
  "Creates a foreign key name from a class name. The default separator
  \"_\" is placed between the name and \"id\".\n
  Examples: (foreign-key \"Message\") => \"message_id\"
            (foreign-key \"Message\" false) => \"messageid\"
            (foreign-key \"Admin::Post\") => \"post_id\""
  ([word] (foreign-key word "_"))
  ([word separator]
     (if-let [word (normalize word)]
       (if-not (blank? word)
         (str (underscore (demodulize word)) (or separator "") "id")))))

(defn hyphenize
  "Returns a string by threading word, which can be a string or a
  symbol, through the functions str, underscore and dasherize.

Examples:

  (camelcase->identifier 'Continent)
  ; => \"continent\"

  (camelcase->identifier \"CountryFlag\")
  ; => \"country-flag\""
  [word]
  (if-let [word (normalize word)]
    (-> word str underscore dasherize)))

(defn parameterize
  "Replaces special characters in a string so that it may be used as
part of a pretty URL.

Examples:

  (parameterize \"Donald E. Knuth\")
  ; => \"donald-e-knuth\"

  (parameterize \"Donald E. Knuth\" \"_\")
  ; => \"donald_e_knuth\"
"
  [string & [separator]]
  (if-let [string (normalize string)]
    (let [separator (or separator "-")]
      (->> string
           (replace-re #"(?i)[^a-z0-9]+" separator)
           (replace-re #"\++" separator)
           (replace-re (Pattern/compile (str separator "{2,}")) separator)
           (replace-re (Pattern/compile (str "(?i)(^" separator ")|(" separator "$)")) "")
           lower-case))))
