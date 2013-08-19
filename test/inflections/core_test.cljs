(ns inflections.core-test
  (:require-macros [cemerick.cljs.test :refer [are is deftest]])
  (:require [cemerick.cljs.test :as t]
            [inflections.core :refer [init-inflections camelize camelize-keys capitalize dasherize demodulize foreign-key
                                      hyphenize irregular? ordinalize parameterize plural pluralize singular
                                      uncountable? underscore stringify-keys stringify-values]]
            [inflections.irregular :refer [*irregular-words*]]
            [inflections.uncountable :refer [*uncountable-words*]]))

(defrecord Foo [a_1 b_2])
(defrecord Bar [a-1 b-2])

(deftest test-camelize
  (are [word expected]
    (= (camelize word) expected)
    nil nil
    "" ""
    "active_record" "ActiveRecord"
    'active_record 'ActiveRecord
    :active_record :ActiveRecord
    "active_record/errors" "ActiveRecord::Errors"
    ;; 'active_record/errors 'ActiveRecord::Errors
    :active_record/errors :Errors)
  (are [word expected]
    (= (camelize word :lower) expected)
    nil nil
    "" ""
    "active_record" "activeRecord"
    'active_record 'activeRecord
    :active_record :activeRecord
    "active_record/errors" "activeRecord::Errors"
    ;; 'active_record/errors 'activeRecord::Errors
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

(deftest test-camelize-keys
  (is (nil? (camelize-keys nil)))
  (is (= {} (camelize-keys {})))
  (is (= {:A1 1 :B2 2} (camelize-keys {:a-1 1 :b_2 2})))
  (is (= {:a1 1 :b2 2} (camelize-keys {:a-1 1 :b_2 2} :lower))))

(deftest test-capitalize
  (is (nil? (capitalize nil)))
  (is (= "" (capitalize "")))
  (is (= (capitalize "hello") "Hello"))
  (is (= (capitalize "HELLO") "Hello" ))
  (is (= (capitalize "123ABC") "123abc")))

(deftest test-dasherize
  (is (= nil (dasherize nil)))
  (is (= "" (dasherize "")))
  (is (= "street" (dasherize "street")))
  (is (= "iso-3166-alpha-2" (dasherize "iso_3166_alpha_2"))))

(deftest test-demodulize
  (is (= nil (demodulize nil)))
  (is (= "" (demodulize "")))
  (is (= "MyRecord" (demodulize "inflections.MyRecord")))
  (is (= "Inflections" (demodulize "Inflections")))
  (is (= "String" (demodulize "ActiveRecord::CoreExtensions::String"))))

(deftest test-foreign-key
  (is (nil? (foreign-key nil)))
  (is (nil? (foreign-key "")))
  (is (= "message_id" (foreign-key "Message")))
  (is (= "post_id" (foreign-key "Admin::Post")))
  (is (= "account_id" (foreign-key "MyApplication::Billing::Account")))
  (is (= "account-id" (foreign-key "MyApplication::Billing::Account" "-")))
  (is (= "user-id" (foreign-key "users" "-")))
  (is (= "user-id" (foreign-key "twitter.users" "-"))))

(deftest test-hyphenize
  (is (= nil (hyphenize nil)))
  (is (= "" (hyphenize "")))
  (is (= "street" (hyphenize "street")))
  (is (= "street-address" (hyphenize "streetAddress")))
  (is (= "iso-3166-alpha-2" (hyphenize "iso_3166_alpha_2"))))

(deftest test-irregular?
  (is (not (empty? @*irregular-words*)))
  (is (every? irregular? @*irregular-words*))
  (is (every? irregular? (map keyword @*irregular-words*)))
  (is (every? irregular? (map symbol @*irregular-words*))))

(deftest test-ordinalize
  (is (= nil (ordinalize nil)))
  (is (= "0th" (ordinalize "0")))
  (is (= "1st" (ordinalize "1")))
  (is (= "2nd" (ordinalize "2")))
  (is (= "3rd" (ordinalize "3")))
  (is (= "4th" (ordinalize "4")))
  (is (= "5th" (ordinalize "5")))
  (is (= "6th" (ordinalize "6")))
  (is (= "7th" (ordinalize "7")))
  (is (= "8th" (ordinalize "8")))
  (is (= "9th" (ordinalize "9")))
  (is (= "10th" (ordinalize "10")))
  (is (= "11th" (ordinalize "11")))
  (is (= "12th" (ordinalize "12")))
  (is (= "13th" (ordinalize "13")))
  (is (= "14th" (ordinalize "14")))
  (is (= "20th" (ordinalize "20")))
  (is (= "21st" (ordinalize "21")))
  (is (= "22nd" (ordinalize "22")))
  (is (= "23rd" (ordinalize "23")))
  (is (= "24th" (ordinalize "24")))
  (is (= "100th" (ordinalize "100")))
  (is (= "101st" (ordinalize "101")))
  (is (= "102nd" (ordinalize "102")))
  (is (= "103rd" (ordinalize "103")))
  (is (= "104th" (ordinalize "104")))
  (is (= "110th" (ordinalize "110")))
  (is (= "111th" (ordinalize "111")))
  (is (= "112th" (ordinalize "112")))
  (is (= "113th" (ordinalize "113")))
  (is (= "1000th" (ordinalize "1000")))
  (is (= "1001st" (ordinalize "1001")))
  (is (= "0th" (ordinalize 0)))
  (is (= "1st" (ordinalize 1)))
  (is (= "2nd" (ordinalize 2)))
  (is (= "3rd" (ordinalize 3)))
  (is (= "4th" (ordinalize 4)))
  (is (= "5th" (ordinalize 5)))
  (is (= "6th" (ordinalize 6)))
  (is (= "7th" (ordinalize 7)))
  (is (= "8th" (ordinalize 8)))
  (is (= "9th" (ordinalize 9)))
  (is (= "10th" (ordinalize 10)))
  (is (= "11th" (ordinalize 11)))
  (is (= "12th" (ordinalize 12)))
  (is (= "13th" (ordinalize 13)))
  (is (= "14th" (ordinalize 14)))
  (is (= "20th" (ordinalize 20)))
  (is (= "21st" (ordinalize 21)))
  (is (= "22nd" (ordinalize 22)))
  (is (= "23rd" (ordinalize 23)))
  (is (= "24th" (ordinalize 24)))
  (is (= "100th" (ordinalize 100)))
  (is (= "101st" (ordinalize 101)))
  (is (= "102nd" (ordinalize 102)))
  (is (= "103rd" (ordinalize 103)))
  (is (= "104th" (ordinalize 104)))
  (is (= "110th" (ordinalize 110)))
  (is (= "111th" (ordinalize 111)))
  (is (= "112th" (ordinalize 112)))
  (is (= "113th" (ordinalize 113)))
  (is (= "1000th" (ordinalize 1000)))
  (is (= "1001st" (ordinalize 1001))))

(deftest test-parameterize
  (is (= "donald-e-knuth" (parameterize "Donald E. Knuth")))
  (is (= "random-text-with-bad-characters" (parameterize "Random text with *(bad)* characters")))
  (is (= "trailing-bad-characters" (parameterize "Trailing bad characters!@#")))
  (is (= "leading-bad-characters" (parameterize "!@#Leading bad characters")))
  (is (= "squeeze-separators" (parameterize "Squeeze separators")))
  (is (= "dasherize-underscores" (parameterize "dasherize_underscores")))
  (is (= "test-with-sign" (parameterize "Test with + sign")))
  (is (= "test-with-malformed-utf8" (parameterize "Test with malformed utf8 \251"))))


(deftest test-plural
  (is (= " " (plural " ")))
  (is (= "" (plural "")))
  (is (= "abilities" (plural "ability")))
  (is (= "addresses" (plural "address")))
  (is (= "amenities" (plural "amenity")))
  (is (= "agencies" (plural "agency")))
  (is (= "aliases" (plural "alias")))
  (is (= "analyses" (plural "analysis")))
  (is (= "archives" (plural "archive")))
  (is (= "axes" (plural "axis")))
  (is (= "bases" (plural "basis")))
  (is (= "boxes" (plural "box")))
  (is (= "buffaloes" (plural "buffalo")))
  (is (= "buses" (plural "bus")))
  (is (= "cases" (plural "case")))
  (is (= "categories" (plural "category")))
  (is (= "comments" (plural "comment")))
  (is (= "crises" (plural "crisis")))
  (is (= "databases" (plural "database")))
  (is (= "data" (plural "datum")))
  (is (= "days" (plural "day")))
  (is (= "diagnoses" (plural "diagnosis")))
  (is (= "diagnosis_as" (plural "diagnosis_a")))
  (is (= "dwarves" (plural "dwarf")))
  (is (= "edges" (plural "edge")))
  (is (= "elves" (plural "elf")))
  (is (= "fixes" (plural "fix")))
  (is (= "foobars" (plural "foobar")))
  (is (= "halves" (plural "half")))
  (is (= "horses" (plural "horse")))
  (is (= "houses" (plural "house")))
  (is (= "indices" (plural "index")))
  (is (= "lice" (plural "louse")))
  (is (= "matrices" (plural "matrix")))
  (is (= "matrix_fus" (plural "matrix_fu")))
  (is (= "media" (plural "medium")))
  (is (= "mice" (plural "mouse")))
  (is (= "moves" (plural "move")))
  (is (= "movies" (plural "movie")))
  (is (= "newsletters" (plural "newsletter")))
  (is (= "octopi" (plural "octopus")))
  (is (= "oxen" (plural "ox")))
  (is (= "perspectives" (plural "perspective")))
  (is (= "photos" (plural "photo")))
  (is (= "portfolios" (plural "portfolio")))
  (is (= "prizes" (plural "prize")))
  (is (= "processes" (plural "process")))
  (is (= "queries" (plural "query")))
  (is (= "quizzes" (plural "quiz")))
  (is (= "saves" (plural "safe")))
  (is (= "searches" (plural "search")))
  (is (= "shoes" (plural "shoe")))
  (is (= "stacks" (plural "stack")))
  (is (= "statuses" (plural "status")))
  (is (= "status_codes" (plural "status_code")))
  (is (= "switches" (plural "switch")))
  (is (= "testes" (plural "testis")))
  (is (= "tomatoes" (plural "tomato")))
  (is (= "vertices" (plural "vertex")))
  (is (= "viri" (plural "virus")))
  (is (= "wives" (plural "wife")))
  (is (= "wishes" (plural "wish"))))

(deftest test-plural-with-irregular-words
  (is (= "amenities" (plural "amenity")))
  (is (= "children" (plural "child")))
  (is (= "kine" (plural "cow")))
  (is (= "feet" (plural "foot")))
  (is (= "lice" (plural "louse")))
  (is (= "mailmen" (plural "mailman")))
  (is (= "men" (plural "man")))
  (is (= "mice" (plural "mouse")))
  (is (= "moves" (plural "move")))
  (is (= "oxen" (plural "ox")))
  (is (= "people" (plural "person")))
  (is (= "sexes" (plural "sex")))
  (is (= "teeth" (plural "tooth")))
  (is (= "women" (plural "woman"))))

(deftest test-plural-with-uncountable-words
  (doseq [word @*uncountable-words*]
    (is (= word (plural word)))))

(deftest test-pluralize
  (is (= "2 users" (pluralize 2 "person" "users")))
  (is (= "0 people" (pluralize 0 "person")))
  (is (= "1 person" (pluralize 1 "person")))
  (is (= "2 people" (pluralize 2 "person"))))

(deftest test-singular
  (is (= " " (singular " ")))
  (is (= "" (singular "")))
  (is (= "ability" (singular "abilities")))
  (is (= "address" (singular "addresses")))
  (is (= "agency" (singular "agencies")))
  (is (= "alias" (singular "aliases")))
  (is (= "amenity" (singular "amenities")))
  (is (= "analysis" (singular "analyses")))
  (is (= "archive" (singular "archives")))
  (is (= "axis" (singular "axes")))
  (is (= "basis" (singular "bases")))
  (is (= "box" (singular "boxes")))
  (is (= "buffalo" (singular "buffaloes")))
  (is (= "bus" (singular "buses")))
  (is (= "case" (singular "cases")))
  (is (= "category" (singular "categories")))
  (is (= "comment" (singular "comments")))
  (is (= "crisis" (singular "crises")))
  (is (= "database" (singular "databases")))
  (is (= "datum" (singular "data")))
  (is (= "day" (singular "days")))
  (is (= "diagnosis" (singular "diagnoses")))
  (is (= "dwarf" (singular "dwarves")))
  (is (= "edge" (singular "edges")))
  (is (= "elf" (singular "elves")))
  (is (= "experience" (singular "experiences")))
  (is (= "fix" (singular "fixes")))
  (is (= "foobar" (singular "foobars")))
  (is (= "half" (singular "halves")))
  (is (= "horse" (singular "horses")))
  (is (= "house" (singular "houses")))
  (is (= "index" (singular "indices")))
  (is (= "louse" (singular "lice")))
  (is (= "matrix" (singular "matrices")))
  (is (= "medium" (singular "media")))
  (is (= "mouse" (singular "mice")))
  (is (= "movie" (singular "movies")))
  (is (= "newsletter" (singular "newsletters")))
  (is (= "octopus" (singular "octopi")))
  (is (= "ox" (singular "oxen")))
  (is (= "perspective" (singular "perspectives")))
  (is (= "photo" (singular "photos")))
  (is (= "portfolio" (singular "portfolios")))
  (is (= "prize" (singular "prizes")))
  (is (= "process" (singular "processes")))
  (is (= "query" (singular "queries")))
  (is (= "quiz" (singular "quizzes")))
  (is (= "safe" (singular "saves")))
  (is (= "search" (singular "searches")))
  (is (= "shoe" (singular "shoes")))
  (is (= "stack" (singular "stacks")))
  (is (= "status" (singular "statuses")))
  (is (= "switch" (singular "switches")))
  (is (= "testis" (singular "testes")))
  (is (= "tomato" (singular "tomatoes")))
  (is (= "vertex" (singular "vertices")))
  (is (= "virus" (singular "viri")))
  (is (= "wife" (singular "wives")))
  (is (= "wish" (singular "wishes")))
  (is (= "weather" (singular "weather"))))

(deftest test-uncountable?
  (is (not (empty? @*uncountable-words*)))
  (is (every? uncountable? @*uncountable-words*))
  (is (every? uncountable? (map keyword @*uncountable-words*)))
  (is (every? uncountable? (map symbol @*uncountable-words*))))

(deftest test-underscore
  (is (= nil (underscore nil)))
  (is (= "" (underscore "")))
  (is (= "product" (underscore "Product")))
  (is (= "special_guest" (underscore "SpecialGuest")))
  (is (= "application_controller" (underscore "ApplicationController")))
  (is (= "area51_controller" (underscore "Area51Controller")))
  (is (= "html_tidy" (underscore "HTMLTidy")))
  (is (= "html_tidy_generator" (underscore "HTMLTidyGenerator")))
  (is (= "free_bsd" (underscore "FreeBSD")))
  (is (= "html" (underscore "HTML")))
  (is (= "iso_3166_alpha_2" (underscore "iso-3166-alpha-2"))))

(deftest test-stringify-keys
  (is (= {} (stringify-keys {})))
  (is (= {"name" "Closure"} (stringify-keys {"name" "Closure"})))
  (is (= {"a-1" {"b-2" {"c-3" 1}}} (stringify-keys {"a-1" {"b-2" {"c-3" 1}}})))
  (is (= {"a-1" {"b-2" {"c-3" 1}}} (stringify-keys {'a-1 {'b-2 {'c-3 1}}}) ))
  (is (= {"a-1" {"b-2" {"c-3" 1}}} (stringify-keys {:a-1 {:b-2 {:c-3 1}}}) ))
  (is (= {"a-1" 1 "b-2" {"c-3" 3}} (stringify-keys (Bar. 1 {:c-3 3})))))

(deftest test-stringify-values
  (is (= {} (stringify-values {})))
  (is (= {"name" "Closure"} (stringify-values {"name" "Closure"})))
  (is (= {"a-1" {"b-2" {"c-3" "1"}}} (stringify-values {"a-1" {"b-2" {"c-3" 1}}})))
  (is (= {'a-1 {'b-2 {'c-3 "1"}}} (stringify-values {'a-1 {'b-2 {'c-3 1}}}) ))
  (is (= {:a-1 {:b-2 {:c-3 "1"}}} (stringify-values {:a-1 {:b-2 {:c-3 1}}}) ))
  (is (= (Bar. "1" {:c-3 "3"})  (stringify-values (Bar. 1 {:c-3 3})))))
