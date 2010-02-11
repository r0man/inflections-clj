(ns inflections
  (:require [clojure.contrib.str-utils2 :as str2])
  (:use clojure.contrib.seq-utils))

(def *irregular-words* (atom {}))
(def *uncountable-words* (atom {}))

(defn- normalize-word [word]
  (str2/lower-case (str2/trim (if (symbol? word) (name word) word))))

(defn- parse-integer [string]
  (try (Integer/parseInt string)
       (catch NumberFormatException exception nil)))

(defn- irregular! [singular plural]
  (let [singular (normalize-word singular)]
    (if-not (@*irregular-words* singular)
      (swap! *irregular-words* assoc singular (normalize-word plural)))))

(defmacro irregular [& words]
  (if-not (= (mod (count words) 2) 0)
    (throw (IllegalArgumentException. "Odd number of words given. Pairs of singular and plural words expected.")))
  (doseq [[singular plural] (partition 2 words)]
    (irregular! singular plural)))

(defn irregular? [word]
  (let [word (normalize-word word)]
    (or (includes? (keys @*irregular-words*) word)
        (includes? (vals @*irregular-words*) word))))

(defn- uncountable! [word]
  (let [normalized-word (normalize-word word)]
    (if-not (@*uncountable-words* normalized-word)
      (swap! *uncountable-words* assoc normalized-word word))))

(defmacro uncountable [& words]
  (doseq [word words] (uncountable! word)))

(defn dasherize
  "Replaces underscores with dashes in the string."
  [word]
  (str2/replace word #"_" "-"))

(defn uncountable? [word]
  (not (nil? (@*uncountable-words* (normalize-word word)))))

(defn underscore
  "Makes an underscored, lowercased version from the given word."
  [word]
  (str2/replace word #"[-\s]+" "_"))

(defn ordinalize
  "Turns a number into an ordinal string used to denote the position
  in an ordered sequence such as 1st, 2nd, 3rd, 4th."
  [number]
  (if-let [number (parse-integer number)]
    (if (includes? (range 11 14) (mod number 100))
      (str number "th")
      (let [modulus (mod number 10)]
        (cond
         (= modulus 1) (str number "st")
         (= modulus 2) (str number "nd")
         (= modulus 3) (str number "rd")
         :else (str number "th"))))))

(irregular
 child children
 cow kine
 foot feet
 louse lice
 mailman mailmen
 man men
 mouse mice
 move moves
 ox oxen
 person people
 sex sexes
 tooth teeth
 woman women)

(uncountable
    air alcohol art blood butter cheese chewing coffee
    confusion cotton education electricity entertainment equipment
    experience fiction fish food forgiveness fresh gold gossip grass
    ground gum happiness history homework honey ice information jam
    knowledge lightning liquid literature love luck luggage meat milk
    mist money music news oil oxygen paper patience peanut pepper
    petrol pork power pressure research rice sadness series sheep
    shopping silver snow space species speed steam sugar sunshine tea
    tennis thunder time toothpaste traffic up vinegar washing wine
    wood wool)

