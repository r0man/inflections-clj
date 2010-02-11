(ns inflections.test.uncountable
  (:use clojure.test inflections.uncountable))

(deftest test-add-uncountable-word
  (reset-uncountable-words)
  (add-uncountable-word "air")
  (is (= (count @*uncountable-words*) 1))
  (add-uncountable-word "air")
  (is (= (count @*uncountable-words*) 1)))

(deftest test-delete-uncountable-word
  (reset-uncountable-words)
  (add-uncountable-word "air")
  (is (= (count @*uncountable-words*) 1))
  (delete-uncountable-word "air")
  (is (= (count @*uncountable-words*) 0)))

(deftest test-reset-uncountable-words
  (add-uncountable-word "air")
  (reset-uncountable-words)
  (is (= (count @*uncountable-words*) 0)))

(deftest test-uncountable?
  (reset-uncountable-words)
  (is (not (uncountable? "air")))
  (add-uncountable-word "air")
  (is (uncountable? "air")))

(deftest test-uncountable
  (reset-uncountable-words)
  (uncountable "air" "rice")
  (is (uncountable? "air"))
  (is (uncountable? "rice")))
