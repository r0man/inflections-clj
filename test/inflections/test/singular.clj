(ns inflections.test.singular
  (:use clojure.test inflections.singular))

(deftest test-add-singular-rule
  (reset-singular-rules)
  (add-singular-rule #"s$" "")
  (is (= (count @*singular-rules*) 1)))

(deftest test-reset-singular-rules
  (reset-singular-rules)
  (add-singular-rule #"s$" "")
  (is (= (count @*singular-rules*) 1))
  (reset-singular-rules)
  (is (= (count @*singular-rules*) 0)))

