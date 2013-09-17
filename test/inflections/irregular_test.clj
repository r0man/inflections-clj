(ns inflections.irregular-test
  (:use clojure.test
        inflections.irregular))

;; (deftest test-add-irregular!
;;   (add-irregular! "singular" "plural")
;;   (is (contains? @*irregular-words* "singular"))
;;   (is (contains? @*irregular-words* "plural"))
;;   (delete-irregular! "singular" "plural"))

;; (deftest test-delete-irregular!
;;   (add-irregular! "singular" "plural")
;;   (delete-irregular! "singular" "plural")
;;   (is (not (contains? @*irregular-words* "singular")))
;;   (is (not (contains? @*irregular-words* "plural"))))
