(ns inflections.test.irregular
  (:use clojure.test inflections.irregular))

(deftest test-irregular?
  (is (every? irregular? (keys @*irregular-words*)))
  (is (every? irregular? (vals @*irregular-words*)))
  (are [word]
       (is (= (irregular? word) true))
       "person" "people" "man" "men" "child" "children"))

