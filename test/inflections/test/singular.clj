(ns inflections.test.singular
  (:use clojure.test inflections.singular))

(deftest test-add-singular-rule
  (reset-singular-rules)
  (add-singular-rule #"s$" "")
  (is (= (count @*singular-rules*) 1)))

(deftest test-reset-singular-rules
  (reset-singular-rules)
  (add-singular-rule #"s$" "")
  (is (= (count @*singular-rules*) 1))
  (reset-singular-rules)
  (is (= (count @*singular-rules*) 0)))

(deftest test-singularize
  (init-singular-rules)
  (are [word expected]
       (is (= (singularize word) expected))
       " " " " 
       "" ""
       "abilities" "ability"
       "addresses" "address"
       "agencies" "agency"
       "aliases" "alias"
       "analyses" "analysis"
       "archives" "archive"
       "axes" "axis"
       "bases" "basis"
       "boxes" "box"
       "buffaloes" "buffalo"
       "buses" "bus"
       "cases" "case"
       "categories" "category"
       "comments" "comment"
       "crises" "crisis"
       "databases" "database"
       "data" "datum"
       "days" "day"
       "diagnoses" "diagnosis"
       "dwarves" "dwarf"
       "edges" "edge"
       "elves" "elf"
       "experiences" "experience"
       "fixes" "fix"
       "foobars" "foobar"
       "halves" "half"
       "horses" "horse"
       "houses" "house"
       "indices" "index"
       "lice" "louse"
       "matrices" "matrix"
       "media" "medium"
       "mice" "mouse"
       "movies" "movie"
       "newsletters" "newsletter"
       "octopi" "octopus"
       "oxen" "ox"
       "perspectives" "perspective"
       "photos" "photo"
       "portfolios" "portfolio"
       "prizes" "prize"
       "processes" "process"
       "queries" "query"
       "quizzes" "quiz"
       "saves" "safe"
       "searches" "search"
       "shoes" "shoe"
       "stacks" "stack"
       "statuses" "status"
       "switches" "switch"
       "testes" "testis"
       "tomatoes" "tomato"
       "vertices" "vertex"
       "viri" "virus"
       "wives" "wife"
       "wishes" "wish"
       ))

(deftest test-singularize-with-irregular-words
  (init-singular-rules)
  (are [word expected]
       (is (= (singularize word) expected))
       ;; "moves" "move"
       ;; "status_status" "code_codes"
       ;; "diagnosis_diagnosis" "a_as"
       ;; "matrix_matrix" "fu_fus"
       ))
