(ns inflections.test.number
  (:require [inflections.number :refer [parse-double parse-float parse-integer parse-location]]))

(defn test-parse-double []
  (assert (nil? (parse-double nil)))
  (assert (nil? (parse-double "")))
  (assert (= 1.0 (parse-double "1")))
  (assert (= 10.0 (parse-double "10.0")))
  (assert (= -10.0 (parse-double "-10.0"))))

(defn test-parse-float []
  (assert (nil? (parse-float nil)))
  (assert (nil? (parse-float "")))
  (assert (= 1.0 (parse-float "1")))
  (assert (= 10.0 (parse-float "10.0")))
  (assert (= -10.0 (parse-float "-10.0"))))

(defn test-parse-integer []
  (assert (nil? (parse-integer nil)))
  (assert (nil? (parse-integer "")))
  (assert (= 1 (parse-integer "1")))
  (assert (= 10 (parse-integer "10")))
  (assert (= -10 (parse-integer "-10"))))

(defn test-parse-location []
  (assert (nil? (parse-location nil)))
  (assert (nil? (parse-location "")))
  (assert (nil? (parse-location "a,b")))
  (assert (= {:latitude 1.0 :longitude -2.0} (parse-location "1,-2")))
  (assert (= {:latitude 1.0 :longitude -2.0} (parse-location "1.0,-2.0")))
  (assert (= {:latitude 1.0 :longitude -2.0} (parse-location "1.0 -2.0"))))

(defn test []
  (test-parse-double)
  (test-parse-float)
  (test-parse-integer)
  (test-parse-location))