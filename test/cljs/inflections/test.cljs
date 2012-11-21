(ns inflections.test
  (:require [inflections.test.core :as core]
            [inflections.test.util :as util]))

(defn ^:export run []
  (core/test)
  (util/test)
  0)