(ns inflections.uncountable
  (:use inflections.helper))

(def *uncountable-words* (atom (sorted-set)))

(defn delete-uncountable [word]
  (if (string? word)
    (swap! *uncountable-words* disj (normalize-word word))))

(defn reset-uncountable-words!
  "Resets the uncountable words."
  []
  (reset! *uncountable-words* (sorted-set)))

(defn uncountable?
  "Returns true if the given word is uncountable, else false."
  [word]
  (contains? @*uncountable-words* (normalize-word word)))

(defn uncountable!
  "Adds the given word(s) to the list of uncountable words."
  [& words]
  (doseq [word words]
    (swap! *uncountable-words* conj (normalize-word word))))

(defn init-uncountable-words []
  (reset-uncountable-words!)
  (uncountable!
      "air" "alcohol" "art" "blood" "butter" "cheese" "chewing" "coffee"
      "confusion" "cotton" "education" "electricity" "entertainment" "equipment"
      "experience" "fiction" "fish" "food" "forgiveness" "fresh" "gold" "gossip" "grass"
      "ground" "gum" "happiness" "history" "homework" "honey" "ice" "information" "jam"
      "knowledge" "lightning" "liquid" "literature" "love" "luck" "luggage" "meat" "milk"
      "mist" "money" "music" "news" "oil" "oxygen" "paper" "patience" "peanut" "pepper"
      "petrol" "pork" "power" "pressure" "research" "rice" "sadness" "series" "sheep"
      "shopping" "silver" "snow" "space" "species" "speed" "steam" "sugar" "sunshine" "tea"
      "tennis" "thunder" "time" "toothpaste" "traffic" "up" "vinegar" "washing" "wine"
      "wood" "wool"))
