(ns inflections.test.irregular
  (:use clojure.test inflections.irregular))

(deftest test-add-irregular-word
  (reset-irregular-words)
  (add-irregular-word "man")
  (is (= (seq @*irregular-words*) ["man"]))
  (add-irregular-word "man")
  (add-irregular-word "men")
  (is (= (seq @*irregular-words*) ["man" "men"])))

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
  (irregular! "child" "children")
  (is (irregular? "child"))
  (is (irregular? "children")))

(deftest test-irregular-with-multiple-rules
  (reset-irregular-words)
  (irregular! "child" "children"
             "cow" "kine")
  (is (every? irregular? ["child" "children" "cow" "kine"])))

(deftest test-irregular?
  (reset-irregular-words)
  (is (not (every? irregular? ["child" "children"])))
  (irregular! "child" "children")
  (is (every? irregular? ["child" "children"])))

(deftest test-irregular
  (reset-irregular-words)
  (are [singular plural]
       (do
         (is (not (irregular? singular)))
         (is (not (irregular? plural)))
         (irregular! singular plural)
         (is (irregular? singular))
         (is (irregular? plural)))
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

  

