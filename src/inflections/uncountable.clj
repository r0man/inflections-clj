(ns inflections.uncountable
  (:use [clojure.string :only [lower-case]]))

(def ^{:dynamic true} *uncountable-words*
  (atom #{"air" "alcohol" "art" "blood" "butter" "cheese" "chewing" "coffee"
          "confusion" "cotton" "education" "electricity" "entertainment" "equipment"
          "experience" "fiction" "fish" "food" "forgiveness" "fresh" "gold" "gossip" "grass"
          "ground" "gum" "happiness" "history" "homework" "honey" "ice" "information" "jam"
          "knowledge" "lightning" "liquid" "literature" "love" "luck" "luggage" "meat" "milk"
          "mist" "money" "music" "news" "oil" "oxygen" "paper" "patience" "peanut" "pepper"
          "petrol" "pork" "power" "pressure" "research" "rice" "sadness" "series" "sheep"
          "shopping" "silver" "snow" "space" "species" "speed" "steam" "sugar" "sunshine" "tea"
          "tennis" "thunder" "time" "toothpaste" "traffic" "up" "vinegar" "washing" "wine"
          "wood" "wool"}))

(defprotocol IUncountable
  (add-uncountable! [obj]
    "Adds obj to the set of *uncountable-words*.")
  (countable? [obj]
    "Returns true if obj is countable, otherwise false.")
  (delete-uncountable! [obj]
    "Delete obj from the set of *uncountable-words*.")
  (uncountable? [obj]
    "Returns true if obj is uncountable, otherwise false."))

(extend-type clojure.lang.Keyword
  IUncountable
  (add-uncountable! [k]
    (add-uncountable! (name k)))
  (countable? [s]
    (not (uncountable? s)))
  (delete-uncountable! [k]
    (delete-uncountable! (name k)))
  (uncountable? [k]
    (uncountable? (name k))))

(extend-type String
  IUncountable
  (add-uncountable! [s]
    (swap! *uncountable-words* conj (lower-case s)))
  (countable? [s]
    (not (uncountable? s)))
  (delete-uncountable! [s]
    (swap! *uncountable-words* disj (lower-case s)))
  (uncountable? [s]
    (contains? @*uncountable-words* (lower-case s))))

(extend-type clojure.lang.Symbol
  IUncountable
  (add-uncountable! [k]
    (add-uncountable! (str k)))
  (countable? [s]
    (not (uncountable? s)))
  (delete-uncountable! [k]
    (delete-uncountable! (str k)))
  (uncountable? [k]
    (uncountable? (str k))))
