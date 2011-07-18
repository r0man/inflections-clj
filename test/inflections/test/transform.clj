(ns inflections.test.transform
  (:use clojure.test inflections.transform))

(deftest test-camelize
  (are [word expected]
    (= (camelize word) expected)
    nil nil
    "" ""
    "active_record" "ActiveRecord"
    "active-record" "ActiveRecord"
    "active_record/errors" "ActiveRecord::Errors"))

(deftest test-camelize-with-lower
  (are [word expected]
    (= (camelize word :lower) expected)
    nil nil
    "" ""
    "active_record" "activeRecord"
    "active-record" "activeRecord"
    "active_record/errors" "activeRecord::Errors"
    "product" "product"
    "special_guest" "specialGuest"
    "application_controller" "applicationController"
    "area51_controller" "area51Controller"))

(deftest test-capitalize
  (are [word expected]
    (= (capitalize word) expected)
    nil nil
    "" ""
    "hello" "Hello"
    "HELLO" "Hello"
    "123ABC" "123abc"))

(deftest test-dasherize
  (are [word expected]
    (= (dasherize word) expected)
    nil nil
    "" ""
    "puni_puni" "puni-puni"
    "street"  "street"
    "street_address" "street-address"
    "person_street_address" "person-street-address"
    :iso_3166_alpha_2 :iso-3166-alpha-2))

(deftest test-demodulize
  (are [word expected]
    (= (demodulize word) expected)
    nil nil
    "" ""
    "inflections.MyRecord" "MyRecord"
    "Inflections" "Inflections"
    "ActiveRecord::CoreExtensions::String::Inflections" "Inflections"))

(deftest test-foreign-key
  (are [word expected]
    (= (foreign-key word) expected)
    nil nil
    "" nil
    "Message" "message_id"
    "Admin::Post" "post_id"
    "MyApplication::Billing::Account" "account_id"))

(deftest test-foreign-key-without-underscore
  (are [word expected]
    (= (foreign-key word false) expected)
    "Message" "messageid"
    "Admin::Post" "postid"
    "MyApplication::Billing::Account" "accountid"))

(deftest test-hyphenize
  (are [word expected]
    (= (hyphenize word) expected)
    nil nil
    "" ""
    'street  "street"
    "StreetAddress" "street-address"))

(deftest test-ordinalize
  (are [number expected]
    (= (ordinalize number) expected)
    nil nil
    "" nil
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
    "1001" "1001st"))

(deftest test-underscore
  (are [word expected]
    (= (underscore word) expected)
    nil nil
    "" ""
    "Product" "product"
    "SpecialGuest" "special_guest"
    "ApplicationController" "application_controller"
    "Area51Controller" "area51_controller"
    "HTMLTidy" "html_tidy"
    "HTMLTidyGenerator" "html_tidy_generator"
    "FreeBSD" "free_bsd"
    "HTML" "html"
    :iso-3166-alpha-2 :iso_3166_alpha_2))

(deftest test-underscore-keys
  (are [m expected]
    (is (= expected (underscore-keys m)))
    {:a-1 {:b-2 {:c-3 1}}} {:a_1 {:b_2 {:c_3 1}}}
    {"a-1" {"b-2" {"c-3" 1}}} {"a_1" {"b_2" {"c_3" 1}}}))

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
