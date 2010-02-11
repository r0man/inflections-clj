(ns inflections.test.rules
  (:use clojure.test inflections.rules))

(defn make-example-rule []
  (make-rule #"(n)ews$" "$1ews"))

(deftest test-add-rule!
  (let [rules (atom [])]
    (add-rule! rules (make-example-rule))
    (is (= (count @rules) 1))
    (let [rule (first @rules)]
      (is (= (str (:pattern rule)) "(n)ews$"))
      (is (= (:replacement rule) "$1ews")))
    (add-rule! rules (make-example-rule))
    (is (= (count @rules) 1))))

(deftest test-make-rule
  (let [rule (make-rule #"(n)ews$" "$1ews")]
    (is (= (str (:pattern rule)) "(n)ews$"))
    (is (= (:replacement rule) "$1ews"))))

(deftest test-make-rules-with-single-rule
  (let [{:keys [pattern replacement]} (first (make-rules #"s$" ""))]
    (is (= (str pattern) "s$"))
    (is (= replacement ""))))

(deftest test-make-rules-with-multiple-rules
  (let [[rule-1 rule-2] (make-rules #"s$" "" #"(n)ews$" "$1ews")]
    (is (= (str (:pattern rule-1)) "s$"))
    (is (= (:replacement rule-1) ""))
    (is (= (str (:pattern rule-2)) "(n)ews$"))
    (is (= (:replacement rule-2) "$1ews"))))

(deftest test-make-rules-with-too-view-arguments
  (is (thrown? IllegalArgumentException (make-rules #"s$"))))

(deftest test-resolve-rule
  (let [rule (make-rule #"s$" "")]
    (is (nil? (resolve-rule rule "word")))
    (is (= (resolve-rule rule "words") "word"))))
   
(deftest test-resolve-rules
  (let [rules [(make-rule #"(vir)us$" "$1i") (make-rule #"$" "s")]]
    (is (= (resolve-rules rules "word") "words"))
    (is (= (resolve-rules rules "virus") "viri"))))

(deftest test-reset-rules!
  (let [rules (atom [])]
    (add-rule! rules (make-example-rule))
    (reset-rules! rules)
    (is (= (count @rules) 0))))