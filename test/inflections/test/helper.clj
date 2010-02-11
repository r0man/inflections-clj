(ns inflections.test.helper
  (:use clojure.test inflections.helper))

(deftest test-normalize-word
  (are [word expected]
       (is (= (normalize-word word) expected))
       "apple" "apple"
       "Banana" "banana"))
