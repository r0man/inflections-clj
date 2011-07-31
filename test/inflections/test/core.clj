(ns inflections.test.core
  (:use clojure.test
        [inflections.irregular :only (*irregular-words*)]
        [inflections.uncountable :only (*uncountable-words*)]
        inflections.core))

(deftest test-camelize
  (are [word expected]
    (= (camelize word) expected)
    nil nil
    "" ""
    "active_record" "ActiveRecord"
    'active_record 'ActiveRecord
    :active_record :ActiveRecord
    "active_record/errors" "ActiveRecord::Errors"
    'active_record/errors 'Errors
    :active_record/errors :Errors)
  (are [word expected]
    (= (camelize word :lower) expected)
    nil nil
    "" ""
    "active_record" "activeRecord"
    'active_record 'activeRecord
    :active_record :activeRecord
    "active_record/errors" "activeRecord::Errors"
    'active_record/errors 'errors
    :active_record/errors :errors
    "product" "product"
    'product 'product
    :product :product
    "special_guest" "specialGuest"
    'special_guest 'specialGuest
    :special_guest :specialGuest
    "application_controller" "applicationController"
    'application_controller 'applicationController
    :application_controller :applicationController
    "area51_controller" "area51Controller"
    'area51_controller 'area51Controller
    :area51_controller :area51Controller))

(deftest test-capitalize
  (are [word expected]
    (= (capitalize word) expected)
    nil nil
    "" ""
    "hello" "Hello"
    'hello 'Hello
    :hello :Hello
    "HELLO" "Hello"
    'HELLO 'Hello
    :HELLO :Hello
    "123ABC" "123abc"
    :123ABC :123abc))

(deftest test-dasherize
  (are [word expected]
    (= (dasherize word) expected)
    nil nil
    "" ""
    "puni_puni" "puni-puni"
    'puni_puni 'puni-puni
    :puni_puni :puni-puni
    "street"  "street"
    'street  'street
    :street  :street
    "street_address" "street-address"
    'street_address 'street-address
    :street_address :street-address
    "person_street_address" "person-street-address"
    'person_street_address 'person-street-address
    :person_street_address :person-street-address
    "iso_3166_alpha_2" "iso-3166-alpha-2"
    'iso_3166_alpha_2 'iso-3166-alpha-2
    :iso_3166_alpha_2 :iso-3166-alpha-2))

(deftest test-demodulize
  (are [word expected]
    (= (demodulize word) expected)
    nil nil
    "" ""
    "inflections.MyRecord" "MyRecord"
    'inflections.MyRecord 'MyRecord
    :inflections.MyRecord :MyRecord
    "Inflections" "Inflections"
    'Inflections 'Inflections
    :Inflections :Inflections
    "ActiveRecord::CoreExtensions::String::Inflections" "Inflections"))

(deftest test-foreign-key
  (are [word expected]
    (= (foreign-key word) expected)
    nil nil
    "" nil
    "Message" "message_id"
    'Message 'message_id
    :Message :message_id
    "Admin::Post" "post_id"
    "MyApplication::Billing::Account" "account_id")
  (are [word separator expected]
    (= (foreign-key word separator) expected)
    "Message" ""  "messageid"
    'Message ""  'messageid
    :Message ""  :messageid
    "Message" "-"  "message-id"
    'Message "-"  'message-id
    :Message "-"  :message-id
    "Admin::Post" ""  "postid"
    "MyApplication::Billing::Account" "" "accountid"))

(deftest test-hyphenize
  (are [word expected]
    (= (hyphenize word) expected)
    nil nil
    "" ""
    "street"  "street"
    'street  'street
    :street  :street
    "StreetAddress" "street-address"
    'StreetAddress 'street-address
    :StreetAddress :street-address))

(deftest test-irregular?
  (is (not (empty? @*irregular-words*)))
  (is (every? irregular? @*irregular-words*))
  (is (every? irregular? (map keyword @*irregular-words*)))
  (is (every? irregular? (map symbol @*irregular-words*))))

(deftest test-ordinalize
  (is (thrown? IllegalArgumentException (ordinalize "")))
  (is (thrown? IllegalArgumentException (ordinalize "x")))
  (are [number expected]
    (= (ordinalize number) expected)
    nil nil
    "0" "0th"
    "1" "1st"
    "2" "2nd"
    "3" "3rd"
    "4" "4th"
    "5" "5th"
    "6" "6th"
    "7" "7th"
    "8" "8th"
    "9" "9th"
    "10" "10th"
    "11" "11th"
    "12" "12th"
    "13" "13th"
    "14" "14th"
    "20" "20th"
    "21" "21st"
    "22" "22nd"
    "23" "23rd"
    "24" "24th"
    "100" "100th"
    "101" "101st"
    "102" "102nd"
    "103" "103rd"
    "104" "104th"
    "110" "110th"
    "111" "111th"
    "112" "112th"
    "113" "113th"
    "1000" "1000th"
    "1001" "1001st"
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
  (are [string expected]
    (is (= (parameterize string) expected))
    "Donald E. Knuth" "donald-e-knuth"
    "Random text with *(bad)* characters" "random-text-with-bad-characters"
    "Trailing bad characters!@#" "trailing-bad-characters"
    "!@#Leading bad characters" "leading-bad-characters"
    "Squeeze separators" "squeeze-separators"
    "dasherize_underscores" "dasherize-underscores"
    "Test with + sign" "test-with-sign"
    "Test with malformed utf8 \251" "test-with-malformed-utf8"))

(deftest test-plural
  (are [word expected]
    (do (is (= expected (plural word)))
        (is (= (keyword expected) (plural (keyword word))))
        (is (= (symbol expected) (plural (symbol word)))))
    " " " "
    "" ""
    "ability" "abilities"
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

(deftest test-plural-with-irregular-words
  (are [word expected]
    (do (is (= expected (plural word)))
        (is (= (keyword expected) (plural (keyword word))))
        (is (= (symbol expected) (plural (symbol word)))))
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
  (doseq [word @*uncountable-words*]
    (is (= word (plural word)))
    (is (= (keyword word) (plural (keyword word))))
    (is (= (symbol word) (plural (symbol word))))))

(deftest test-singular
  (are [word expected]
    (do (is (= expected (singular word)))
        (is (= (keyword expected) (singular (keyword word))))
        (is (= (symbol expected) (singular (symbol word)))))
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
    ))

(deftest test-uncountable?
  (is (not (empty? @*uncountable-words*)))
  (is (every? uncountable? @*uncountable-words*))
  (is (every? uncountable? (map keyword @*uncountable-words*)))
  (is (every? uncountable? (map symbol @*uncountable-words*))))

(deftest test-underscore
  (are [word expected]
    (= (underscore word) expected)
    nil nil
    "" ""
    "Product" "product"
    'Product 'product
    :Product :product
    "SpecialGuest" "special_guest"
    'SpecialGuest 'special_guest
    :SpecialGuest :special_guest
    "ApplicationController" "application_controller"
    'ApplicationController 'application_controller
    :ApplicationController :application_controller
    "Area51Controller" "area51_controller"
    'Area51Controller 'area51_controller
    :Area51Controller :area51_controller
    "HTMLTidy" "html_tidy"
    'HTMLTidy 'html_tidy
    :HTMLTidy :html_tidy
    "HTMLTidyGenerator" "html_tidy_generator"
    'HTMLTidyGenerator 'html_tidy_generator
    :HTMLTidyGenerator :html_tidy_generator
    "FreeBSD" "free_bsd"
    'FreeBSD 'free_bsd
    :FreeBSD :free_bsd
    "HTML" "html"
    'HTML 'html
    :HTML :html
    "iso-3166-alpha-2" "iso_3166_alpha_2"
    'iso-3166-alpha-2 'iso_3166_alpha_2
    :iso-3166-alpha-2 :iso_3166_alpha_2))

(deftest test-underscore-keys
  (are [m expected]
    (is (= expected (underscore-keys m)))
    {"a-1" {"b-2" {"c-3" 1}}} {"a_1" {"b_2" {"c_3" 1}}}
    {'a-1 {'b-2 {'c-3 1}}} {'a_1 {'b_2 {'c_3 1}}}
    {:a-1 {:b-2 {:c-3 1}}} {:a_1 {:b_2 {:c_3 1}}}))
