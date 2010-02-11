(ns inflections.test.uncountable
  (:use clojure.test inflections.uncountable))

(deftest test-add-uncountable-word
  (reset-uncountable-words)
  (add-uncountable-word "air")
  (is (= (seq @*uncountable-words*) ["air"]))
  (add-uncountable-word "air")
  (add-uncountable-word "rice")
  (is (= (seq @*uncountable-words*) ["air" "rice"])))

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
  (init-uncountable-words)
  (is (every? uncountable? @*uncountable-words*)))
