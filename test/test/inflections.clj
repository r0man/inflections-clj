(ns test.inflections
  (:use clojure.test inflections))

(deftest test-dasherize
  (are [word expected] (= (dasherize word) expected)
       "puni_puni" "puni-puni"))

(deftest test-uncountable?
  (is (every? uncountable? (keys @*uncountable-words*)))
  (are [word] (is (= (uncountable? word) true))
       "air" "alcohol" "art" "blood" "butter" "cheese"))

(deftest test-underscore
  (are [word expected] (= (underscore word) expected)
       "puni-puni" "puni_puni"
       "puni puni" "puni_puni"))


