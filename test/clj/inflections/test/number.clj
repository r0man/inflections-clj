(ns inflections.test.number
  (:use clojure.test
        inflections.number))

(deftest test-parse-float
  (is (nil? (parse-float nil)))
  (is (nil? (parse-float "")))
  (is (= 1.0 (parse-float "1")))
  (is (= 10.0 (parse-float "10.0")))
  (is (= -10.0 (parse-float "-10.0"))))

(deftest test-parse-integer
  (is (nil? (parse-integer nil)))
  (is (nil? (parse-integer "")))
  (is (= 1 (parse-integer "1")))
  (is (= 10 (parse-integer "10")))
  (is (= -10 (parse-integer "-10"))))

(deftest test-parse-long
  (is (nil? (parse-long nil)))
  (is (nil? (parse-long "")))
  (is (= 1 (parse-long "1")))
  (is (= 10 (parse-long "10")))
  (is (= -10 (parse-long "-10"))))