(ns inflections.test.helper
  (:use clojure.test inflections.uncountable))

(deftest test-uncountable?
  (is (every? uncountable? @*uncountable-words*))
  (are [word]
       (is (= (uncountable? word) true))
       "air" "alcohol" "art" "blood" "butter" "cheese"))

