(ns inflections.core-test
  (:require [inflections.core :as c]
            #?(:clj [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros [are is deftest]])))

(defrecord Foo [a_1 b_2])
(defrecord Bar [a-1 b-2])

(deftest test-coerce
  (are [obj s expected]
      (= expected (c/coerce obj s))
    nil :x  :x
    :x nil nil
    nil nil nil
    :x "x" :x
    'x "x" 'x
    "x" "x" "x"))

(deftest test-str-name
  (are [x expected]
      (= expected (c/str-name x))
    nil nil
    "" ""
    "x" "x"
    :x "x"
    'x "x"
    :x/y "x/y"
    'x/y "x/y"))

(deftest test-camel-case
  (are [word expected]
      (= (c/camel-case word) expected)
    nil nil
    "" ""
    "active_record" "ActiveRecord"
    "active_record/errors" "ActiveRecord/Errors")
  (are [word expected]
      (= (c/camel-case word :lower) expected)
    nil nil
    "" ""
    "active_record" "activeRecord"
    :active_record :activeRecord
    'active_record 'activeRecord
    "active_record/errors" "activeRecord/Errors"
    "product" "product"
    "special_guest" "specialGuest"
    "application_controller" "applicationController"
    "area51_controller" "area51Controller"))

(deftest test-camel-case-keys
  (is (nil? (c/camel-case-keys nil)))
  (is (= {} (c/camel-case-keys {})))
  (is (= {:A1 1 :B2 2} (c/camel-case-keys {:a-1 1 :b_2 2})))
  (is (= {:a1 1 :b2 2} (c/camel-case-keys {:a-1 1 :b_2 2} :lower))))

(deftest test-capitalize
  (c/add-acronym! "HST")
  (c/add-acronym! "PhDs")
  (are [word expected]
      (= (c/capitalize word) expected)
    nil nil
    "" ""
    "hello" "Hello"
    'hello 'Hello
    :hello :Hello
    "HELLO" "Hello"
    "123ABC" "123abc"
    "hsts" "Hsts"
    "phds" "PhDs"))

(deftest test-dasherize
  (are [word expected]
      (= (c/dasherize word) expected)
    nil nil
    "" ""
    "puni_puni" "puni-puni"
    'puni_puni 'puni-puni
    :puni_puni :puni-puni
    "street" "street"
    "street_address" "street-address"
    "person_street_address" "person-street-address"
    "iso_3166_alpha_2" "iso-3166-alpha-2"))

(deftest test-demodulize
  (are [word expected]
      (= (c/demodulize word) expected)
    nil nil
    "" ""
    "inflections.MyRecord" "MyRecord"
    'inflections.MyRecord 'MyRecord
    :inflections.MyRecord :MyRecord
    "Inflections" "Inflections"
    "ActiveRecord::CoreExtensions::String::Inflections" "Inflections"))

(deftest test-foreign-key
  (are [word expected]
      (= (c/foreign-key word) expected)
    nil nil
    "" nil
    "Message" "message_id"
    'Message 'message_id
    :Message :message_id
    "Admin::Post" "post_id"
    "MyApplication::Billing::Account" "account_id")
  (are [word separator expected]
      (= (c/foreign-key word separator) expected)
    "Message" ""  "messageid"
    "Message" "-"  "message-id"
    "Admin::Post" ""  "postid"
    "MyApplication::Billing::Account" "" "accountid"
    "users" "_" "user_id"
    "twitter.users" "_" "user_id"))

(deftest test-hyphenate
  (are [obj expected]
      (= (c/hyphenate obj) expected)
    nil nil
    "" ""
    "-" "-"
    "_" "-"
    "street" "street"
    "StreetAddress" "street-address"
    'StreetAddress 'street-address
    :StreetAddress :street-address
    "Street Address" "street-address"
    "SpecialGuest" "special-guest"
    "ApplicationController" "application-controller"
    "ActiveRecord::Base" "active-record::base"
    "Area51Controller" "area51-controller"
    "HTMLTidy" "html-tidy"
    "HTMLTidyGenerator" "html-tidy-generator"
    "FreeBSD" "free-bsd"
    "HTML" "html"
    "iso-3166-alpha-2" "iso-3166-alpha-2"))

(deftest test-irregular?
  (is (nil? (c/irregular? nil)))
  (is (not (empty? @c/*irregular-words*)))
  (is (every? c/irregular? @c/*irregular-words*))
  (is (every? c/irregular? (map keyword @c/*irregular-words*)))
  (is (every? c/irregular? (map symbol @c/*irregular-words*))))

(deftest test-ordinalize
  (are [number expected]
      (= expected (c/ordinalize number))
    nil nil
    "" nil
    "x" nil
    0 "0th"
    1 "1st"
    2 "2nd"
    3 "3rd"
    4 "4th"
    5 "5th"
    6 "6th"
    7 "7th"
    8 "8th"
    9 "9th"
    10 "10th"
    11 "11th"
    12 "12th"
    13 "13th"
    14 "14th"
    20 "20th"
    21 "21st"
    22 "22nd"
    23 "23rd"
    24 "24th"
    100 "100th"
    101 "101st"
    102 "102nd"
    103 "103rd"
    104 "104th"
    110 "110th"
    111 "111th"
    112 "112th"
    113 "113th"
    1000 "1000th"
    1001 "1001st"))

(deftest test-parameterize
  (are [obj expected]
      (= (c/parameterize obj) expected)
    "Donald E. Knuth" "donald-e-knuth"
    "Random text with *(bad)* characters" "random-text-with-bad-characters"
    "Trailing bad characters!@#" "trailing-bad-characters"
    "!@#Leading bad characters" "leading-bad-characters"
    "Squeeze separators" "squeeze-separators"
    "dasherize_underscores" "dasherize-underscores"
    "Test with + sign" "test-with-sign"
    "Test with malformed utf8 \251" "test-with-malformed-utf8"
    :a_1 "a-1"))

(deftest test-plural
  (are [word expected]
      (= expected (c/plural word))
    nil nil
    " " " "
    "" ""
    "ability" "abilities"
    :ability "abilities"
    'ability "abilities"
    "address" "addresses"
    "amenity" "amenities"
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
    "wish" "wishes"))

(deftest test-pluralize
  (is (= "2 users" (c/pluralize 2 "person" "users")))
  (are [count word expected]
      (= expected (c/pluralize count word))
    0 "person" "0 people"
    1 "person" "1 person"
    2 "person" "2 people"))

(deftest test-plural-with-irregular-words
  (are [word expected]
      (= expected (c/plural word))
    "amenity" "amenities"
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

(deftest test-plural-with-uncountable-words
  (doseq [word @c/*uncountable-words*]
    (is (= word (c/plural word)))))

(deftest test-singular
  (are [word expected]
      (= expected (c/singular word))
    nil nil
    " " " "
    "" ""
    "abilities" "ability"
    :abilities :ability
    'abilities 'ability
    "addresses" "address"
    "address" "address"
    "agencies" "agency"
    "aliases" "alias"
    "alias" "alias"
    "amenities" "amenity"
    "analyses" "analysis"
    "analysis" "analysis"
    "archives" "archive"
    "axes" "axis"
    "bases" "basis"
    "boxes" "box"
    "buffaloes" "buffalo"
    "buses" "bus"
    "bus" "bus"
    "cases" "case"
    "categories" "category"
    "comments" "comment"
    "crises" "crisis"
    "crisis" "crisis"
    "databases" "database"
    "data" "datum"
    "days" "day"
    "diagnoses" "diagnosis"
    "diagnosis" "diagnosis"
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
    "octopus" "octopus"
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
    "status" "status"
    "switches" "switch"
    "testes" "testis"
    "tomatoes" "tomato"
    "vertices" "vertex"
    "viri" "virus"
    "wives" "wife"
    "wishes" "wish"
    "weather" "weather"))

(deftest test-uncountable?
  (is (not (c/uncountable? nil)))
  (is (not (empty? @c/*uncountable-words*)))
  (is (every? c/uncountable? @c/*uncountable-words*))
  (is (every? c/uncountable? (map keyword @c/*uncountable-words*)))
  (is (every? c/uncountable? (map symbol @c/*uncountable-words*))))

(deftest test-underscore
  (are [word expected]
      (= (c/underscore word) expected)
    nil nil
    "" ""
    "weather.nww3-htsgwsfc-2013-02-04T00" "weather.nww3_htsgwsfc_2013_02_04_t00"
    "ActiveRecord" "active_record"
    :ActiveRecord :active_record
    'ActiveRecord 'active_record
    "ActiveRecord::Errors" "active_record::errors"
    :titles/site-name :titles/site_name))

(deftest test-stringify-keys
  (are [m expected]
      (is (= expected (c/stringify-keys m)))
    nil nil
    {} {}
    {"name" "Closure"} {"name" "Closure"}
    {"a-1" {"b-2" {"c-3" 1}}} {"a-1" {"b-2" {"c-3" 1}}}
    {'a-1 {'b-2 {'c-3 1}}}  {"a-1" {"b-2" {"c-3" 1}}}
    {:a-1 {:b-2 {:c-3 1}}}  {"a-1" {"b-2" {"c-3" 1}}}
    (Bar. 1 {:c-3 3}) {"a-1" 1 "b-2" {"c-3" 3}}))

(deftest test-stringify-values
  (are [m expected]
      (is (= expected (c/stringify-values m)))
    nil nil
    {} {}
    {"name" "Closure"} {"name" "Closure"}
    {"a-1" {"b-2" {"c-3" 1}}} {"a-1" {"b-2" {"c-3" "1"}}}
    {'a-1 {'b-2 {'c-3 1}}}  {'a-1 {'b-2 {'c-3 "1"}}}
    {:a-1 {:b-2 {:c-3 1}}}  {:a-1 {:b-2 {:c-3 "1"}}}
    (Bar. 1 {:c-3 3}) (Bar. "1" {:c-3 "3"}) ))

(deftest test-underscore-keys
  (are [m expected]
      (is (= expected (c/underscore-keys m)))
    nil nil
    {} {}
    {"a-1" {"b-2" {"c-3" 1}}} {"a_1" {"b_2" {"c_3" 1}}}
    {'a-1 {'b-2 {'c-3 1}}}  {'a_1 {'b_2 {'c_3 1}}}
    {:a-1 {:b-2 {:c-3 1}}}  {:a_1 {:b_2 {:c_3 1}}}
    (Bar. 1 {:c-3 3}) {:a_1 1, :b_2 {:c_3 3}}))

(deftest test-hyphenate-keys
  (are [m expected]
      (is (= expected (c/hyphenate-keys m)))
    nil nil
    {} {}
    {"a_1" {:b_2 {'c_3 1}}} {"a-1" {:b-2 {'c-3 1}}}
    (Bar. 1 {:c_3 3}) {:a-1 1, :b-2 {:c-3 3}}))

(deftest test-acronym
  (is (= "HST" (c/acronym "hst")))
  (c/delete-acronym! "hst")
  (is (nil? (c/acronym "hst")))
  (c/add-acronym! "HsT")
  (are [word expected]
      (= expected (c/acronym word))
    nil nil
    "blog" nil
    "hst" "HsT"
    "NASA" "NASA"
    "nasa" "NASA"
    "nAsa" "NASA"
    :nasa :NASA
    'nasa 'NASA))

(deftest test-titleize
  (c/add-acronym! "HST")
  (are [word expected]
      (is (= expected (c/titleize word)))
    " " ""
    "" ""
    "hello world" "Hello World"
    "blog-post" "Blog Post"
    "nasa-budget" "NASA Budget"
    "included-HST-amount" "Included HST Amount"
    "word" "Word"
    "hst" "HST"))
