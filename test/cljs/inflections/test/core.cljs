(ns inflections.test.core
  (:require [inflections.core :refer [camelize capitalize dasherize demodulize foreign-key]]))

(defn test-camelize []
  ;; (assert (nil? (camelize nil)))
  ;; (assert (= "" (camelize "")))
  ;; (assert (= "active_record" (camelize "ActiveRecord")))
  ;; (assert (= 'active_record (camelize 'ActiveRecord)))
  ;; (assert (= :active_record (camelize :ActiveRecord)))
  ;; (assert (= "active_record/errors" (camelize "ActiveRecord::Errors")))
  ;; (assert (= 'active_record/errors (camelize 'Errors)))
  ;; (assert (= :active_record/errors (camelize :Errors)))
  )

(defn test-capitalize []
  (assert (nil? (capitalize nil)))
  (assert (= "" (capitalize "")))
  (assert (= (capitalize "hello") "Hello"))
  (assert (= (capitalize "HELLO") "Hello" ))
  (assert (= (capitalize "123ABC") "123abc")))

(defn test-dasherize []
  (assert (= nil (dasherize nil)))
  (assert (= "" (dasherize "")))
  (assert (= "street" (dasherize "street")))
  (assert (= "iso-3166-alpha-2" (dasherize "iso_3166_alpha_2"))))

(defn test-demodulize []
  (assert (= nil (demodulize nil)))
  (assert (= "" (demodulize "")))
  (assert (= "MyRecord" (demodulize "inflections.MyRecord")))
  (assert (= "Inflections" (demodulize "Inflections")))
  (assert (= "String" (demodulize "ActiveRecord::CoreExtensions::String"))))

(defn test-foreign-key []
  (assert (nil? (foreign-key nil)))
  (assert (nil? (foreign-key "")))
  (assert (= "message_id" (foreign-key "Message")))
  (assert (= "post_id" (foreign-key "Admin::Post")))
  (assert (= "account_id" (foreign-key "MyApplication::Billing::Account"))))

(defn test []
  (test-camelize)
  (test-capitalize)
  (test-dasherize)
  (test-demodulize)
  (test-foreign-key))
