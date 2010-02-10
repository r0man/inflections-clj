(ns inflections
  (:require [clojure.contrib.str-utils2 :as str2]))

(def *uncountable-words* (atom []))

(defn dasherize [word]
  (str2/replace word #"_" "-"))

(defn underscore [word]
  (str2/replace word #"[-\s]+" "_"))

