(ns inflections.number)

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
