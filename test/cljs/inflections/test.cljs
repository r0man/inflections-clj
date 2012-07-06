(ns inflections.test
  (:require [inflections.test.core :as core]
            [inflections.test.number :as number]))

(defn ^:export run []
  (core/test)
  (number/test)
  "All tests passed.")