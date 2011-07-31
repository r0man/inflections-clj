(ns inflections.uncountable
  (:use [clojure.string :only (lower-case)]))

(def ^:dynamic *uncountable-words*
  (atom (sorted-set)))

(defprotocol Uncountable
  (add-uncountable! [obj]
    "Adds obj to the set of *uncountable-words*.")
  (delete-uncountable! [obj]
    "Delete obj from the set of *uncountable-words*.")
  (uncountable? [obj]
    "Returns true if obj is an uncountable word, otherwise false."))

(extend-type clojure.lang.Keyword
  Uncountable
  (add-uncountable! [k]
    (add-uncountable! (name k)))
  (delete-uncountable! [k]
    (delete-uncountable! (name k)))
  (uncountable? [k]
    (uncountable? (name k))))

(extend-type String
  Uncountable
  (uncountable? [s]
    (contains? @*uncountable-words* (lower-case s)))
  (add-uncountable! [s]
    (swap! *uncountable-words* conj (lower-case s)))
  (delete-uncountable! [s]
    (swap! *uncountable-words* disj (lower-case s))))

(extend-type clojure.lang.Symbol
  Uncountable
  (add-uncountable! [k]
    (add-uncountable! (str k)))
  (delete-uncountable! [k]
    (delete-uncountable! (str k)))
  (uncountable? [k]
    (uncountable? (str k))))

(defn init-uncountable-words []
  (doall
   (map add-uncountable!
        ["air" "alcohol" "art" "blood" "butter" "cheese" "chewing" "coffee"
         "confusion" "cotton" "education" "electricity" "entertainment" "equipment"
         "experience" "fiction" "fish" "food" "forgiveness" "fresh" "gold" "gossip" "grass"
         "ground" "gum" "happiness" "history" "homework" "honey" "ice" "information" "jam"
         "knowledge" "lightning" "liquid" "literature" "love" "luck" "luggage" "meat" "milk"
         "mist" "money" "music" "news" "oil" "oxygen" "paper" "patience" "peanut" "pepper"
         "petrol" "pork" "power" "pressure" "research" "rice" "sadness" "series" "sheep"
         "shopping" "silver" "snow" "space" "species" "speed" "steam" "sugar" "sunshine" "tea"
         "tennis" "thunder" "time" "toothpaste" "traffic" "up" "vinegar" "washing" "wine"
         "wood" "wool"])))
