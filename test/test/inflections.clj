(ns test.inflections
  (:use clojure.test inflections))

(deftest test-dasherize
  (are [word expected] (= (dasherize word) expected)
       "puni_puni" "puni-puni"))

(deftest test-underscore
  (are [word expected] (= (underscore word) expected)
       "puni-puni" "puni_puni"
       "puni puni" "puni_puni"))


