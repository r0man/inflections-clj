(ns inflections.test.uncountable
  (:use clojure.test inflections.uncountable))

(deftest test-delete-uncountable
  (reset-uncountable-words!)
  (uncountable! "air")
  (is (= (count @*uncountable-words*) 1))
  (delete-uncountable "air")
  (is (= (count @*uncountable-words*) 0)))

(deftest test-reset-uncountable-words!
  (uncountable! "air")
  (reset-uncountable-words!)
  (is (= (count @*uncountable-words*) 0)))

(deftest test-uncountable?
  (reset-uncountable-words!)
  (is (not (uncountable? "air")))
  (uncountable! "air")
  (is (uncountable? "air")))

(deftest test-uncountable
  (init-uncountable-words)
  (is (every? uncountable? @*uncountable-words*)))
