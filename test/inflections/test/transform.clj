(ns inflections.test.transform
  (:use clojure.test inflections.transform))

(deftest test-capitalize
  (are [word expected]
       (= (capitalize word) expected)
       "hello" "Hello"
       "HELLO" "Hello"
       "123ABC" "123abc"))

(deftest test-dasherize
  (are [word expected]
       (= (dasherize word) expected)
       "puni_puni" "puni-puni"
       "street"  "street"
       "street_address" "street-address"
       "person_street_address" "person-street-address"))

(deftest test-ordinalize
  (are [number expected]
       (= (ordinalize number) expected)
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
       "Product" "product"
       "SpecialGuest" "special_guest"
       "ApplicationController" "application_controller"
       "Area51Controller" "area51_controller"
       "HTMLTidy" "html_tidy"
       "HTMLTidyGenerator" "html_tidy_generator"
       "FreeBSD" "free_bsd"
       "HTML" "html"))
