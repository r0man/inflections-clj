(ns inflections.test.core
  (:require [inflections.core :refer [camelize capitalize dasherize]]))

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

(defn test []
  (test-camelize)
  (test-capitalize)
  (test-dasherize))