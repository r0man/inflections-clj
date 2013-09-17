(ns inflections.uncountable-test
  (:use clojure.test
        inflections.uncountable))

;; (deftest test-add-uncountable
;;   (is (not (uncountable? "test")))
;;   (add-uncountable! "test")
;;   (is (contains? @*uncountable-words* "test"))
;;   (is (uncountable? "test"))
;;   (delete-uncountable! "test"))

;; (deftest test-delete-uncountable!
;;   (add-uncountable! "test")
;;   (is (contains? @*uncountable-words* "test"))
;;   (delete-uncountable! "test")
;;   (is (not (contains? @*uncountable-words* "test"))))
