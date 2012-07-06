(ns inflections.test.number
  (:require [inflections.number :refer [parse-float parse-integer]]))

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

(defn test []
  (test-parse-float)
  (test-parse-integer))