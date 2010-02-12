(ns inflections.test.plural
  (:use clojure.test
        inflections.irregular
        inflections.plural
        inflections.rules
        inflections.uncountable))

(deftest test-plural-with-single-rule
  (with-reset-rules *plural-rules*
    (plural #"$" "s")
    (is (= (count @*plural-rules*) 1))
    (let [rule (first @*plural-rules*)]
      (is (= (str (:pattern rule)) "$"))
      (is (= (:replacement rule) "s")))))

(deftest test-plural-with-multiple-rule
  (with-reset-rules *plural-rules*
    (plural #"$" "s" #"s$" "s")
    (is (= (count @*plural-rules*) 2))
    (let [rule (first @*plural-rules*)]
      (is (= (str (:pattern rule)) "$"))
      (is (= (:replacement rule) "s")))
    (let [rule (last @*plural-rules*)]
      (is (= (str (:pattern rule)) "s$"))
      (is (= (:replacement rule) "s")))))

(deftest test-pluralize
  (with-reset-rules *plural-rules*
    (init-plural-rules)
    (are [word expected]
         (is (= (pluralize word) expected))
         " " " " 
         "" ""
         "ability" "abilities"
         "address" "addresses"
         "agency" "agencies"
         "alias" "aliases"
         "analysis" "analyses"
         "archive" "archives"
         "axis" "axes"
         "basis" "bases"
         "box" "boxes"
         "buffalo" "buffaloes"
         "bus" "buses"
         "case" "cases"
         "category" "categories"
         "comment" "comments"
         "crisis" "crises"
         "database" "databases"
         "datum" "data"
         "day" "days"
         "diagnosis" "diagnoses"
         "diagnosis_a" "diagnosis_as"
         "dwarf" "dwarves"
         "edge" "edges"
         "elf" "elves"
         "fix" "fixes"
         "foobar" "foobars"
         "half" "halves"
         "horse" "horses"
         "house" "houses"
         "index" "indices"
         "louse" "lice"
         "matrix" "matrices"
         "matrix_fu" "matrix_fus"
         "medium" "media"
         "mouse" "mice"
         "move" "moves"
         "movie" "movies"
         "newsletter" "newsletters"
         "octopus" "octopi"
         "ox" "oxen"
         "perspective" "perspectives"
         "photo" "photos"
         "portfolio" "portfolios"
         "prize" "prizes"
         "process" "processes"
         "query" "queries"
         "quiz" "quizzes"
         "safe" "saves"
         "search" "searches"
         "shoe" "shoes"
         "stack" "stacks"
         "status" "statuses"
         "status_code" "status_codes"
         "switch" "switches"
         "testis" "testes"
         "tomato" "tomatoes"
         "vertex" "vertices"
         "virus" "viri"
         "wife" "wives"
         "wish" "wishes")))

(deftest test-pluralize-with-irregular-words
  (with-reset-rules *plural-rules*
    (init-plural-rules)
    (init-irregular-words)
    (are [word expected]
         (is (= (pluralize word) expected))
         "child" "children"
         "cow" "kine"
         "woman" "women"
         "salesperson" "salespeople"
         "spokesman" "spokesmen"
         "node_child" "node_children"
         "person" "people"
         "man" "men")))

(deftest test-pluralize-with-uncountable-words
  (with-reset-rules *plural-rules*
    (init-plural-rules)
    (init-uncountable-words)
    (are [word expected]
         (is (= (pluralize word) expected))
         "experience" "experience"
         "series" "series"
         "species" "species"
         "news" "news"
         "rice" "rice"
         "equipment" "equipment"
         "fish" "fish"
         "information" "information")))
