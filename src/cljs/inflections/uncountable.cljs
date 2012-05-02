(ns inflections.uncountable
  (:use [clojure.string :only (lower-case)]))

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
  (delete-uncountable! [obj]
    "Delete obj from the set of *uncountable-words*.")
  (uncountable? [obj]
    "Returns true if obj is uncountable, otherwise false."))

(extend-type string
  IUncountable
  (uncountable? [s]
    (contains? @*uncountable-words* (lower-case (name s))))
  (add-uncountable! [s]
    (swap! *uncountable-words* conj (lower-case (name s))))
  (delete-uncountable! [s]
    (swap! *uncountable-words* disj (lower-case (name s)))))
