(ns inflections.test.singular
  (:use clojure.test inflections.irregular inflections.rules inflections.singular inflections.uncountable))

(deftest test-singularize
  (with-reset-rules *singular-rules*
    (init-singular-rules)
    (are [word expected]
        (is (= (singularize word) expected))
        " " " "
        "" ""
        "abilities" "ability"
        "addresses" "address"
        "agencies" "agency"
        "aliases" "alias"
        "amenities" "amenity"
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
        )))

(deftest test-singularize-with-irregular-words
  (with-reset-rules *singular-rules*
    (init-singular-rules)
    (init-irregular-words)
    (are [word expected]
        (is (= (singularize word) expected))
        "moves" "move"
        "children" "child")))

(deftest test-singularize-with-uncountable-words
  (with-reset-rules *singular-rules*
    (init-singular-rules)
    (init-uncountable-words)
    (are [word expected]
        (is (= (singularize word) expected))
        "air" "air"
        "alcohol" "alcohol")))

