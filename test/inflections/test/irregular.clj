(ns inflections.test.irregular
  (:use clojure.test inflections.irregular))

(deftest test-add-irregular-word
  (reset-irregular-words)
  (add-irregular-word "man")
  (is (= (seq @*irregular-words*) ["man"]))
  (add-irregular-word "men")
  (is (= (seq @*irregular-words*) ["men" "man"])))

(deftest test-delete-irregular-word
  (reset-irregular-words)
  (add-irregular-word "man")
  (is (= (seq @*irregular-words*) ["man"]))
  (delete-irregular-word "man")
  (is (nil?  (seq @*irregular-words*))))

(deftest test-reset-irregular-words
  (add-irregular-word "man")
  (reset-irregular-words)
  (is (nil?  (seq @*irregular-words*))))

(deftest test-irregular-with-single-rule
  (reset-irregular-words)
  (irregular "child" "children")
  (is (= @*irregular-words* #{"child" "children"})))

(deftest test-irregular-with-multiple-rule
  (reset-irregular-words)
  (irregular "child" "children" "cow" "kine")
  (is (= @*irregular-words* #{"cow" "kine" "child" "children"})))

(deftest test-irregular?
  (init-irregular-words)
  (are [word] (is (irregular? word))
       "child" "children"
       "cow" "kine"
       "foot" "feet"
       "louse" "lice"
       "mailman" "mailmen"
       "man" "men"
       "mouse" "mice"
       "move" "moves"
       "ox" "oxen"
       "person" "people"
       "sex" "sexes"
       "tooth" "teeth"
       "woman" "women"))

