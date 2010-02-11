(ns inflections.test.rules
  (:use clojure.test inflections.rules))

(deftest test-match-rule
  (let [rule (make-rule #"s$" "")]
    (is (nil? (match-rule rule "word")))
    (is (= (match-rule rule "words") "word"))))

(deftest test-map-rules-with-single-rule
  (let [{:keys [pattern replacement]} (first (map-rules #"s$" ""))]
    (is (= (str pattern) "s$"))
    (is (= replacement ""))))

(deftest test-map-rules-with-multiple-rules
  (let [[rule-1 rule-2] (map-rules #"s$" "" #"(n)ews$" "$1ews")]
    (is (= (str (:pattern rule-1)) "s$"))
    (is (= (:replacement rule-1) ""))
    (is (= (str (:pattern rule-2)) "(n)ews$"))
    (is (= (:replacement rule-2) "$1ews"))))

(deftest test-map-rules-with-too-view-arguments
  (is (thrown? IllegalArgumentException (map-rules #"s$"))))