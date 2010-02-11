(ns inflections.uncountable
  (:use inflections.helper))

(def *uncountable-words* (atom (set [])))

(defn add-uncountable-word
  "Adds a the given word to the list of uncountable words."
  [word]
  (swap! *uncountable-words* conj (normalize-word word)))

(defn delete-uncountable-word
  "Deletes the given word from the list of uncountable words."
  [word]
  (swap! *uncountable-words* disj (normalize-word word)))

(defn reset-uncountable-words
  "Resets the set of uncountable words."
  []
  (reset! *uncountable-words* (set [])))

(defn uncountable?
  "Returns true if the given word is uncountable, else false."
  [word]
  (contains? @*uncountable-words* (normalize-word word)))

(defn uncountable
  "Adds the given word(s) to the list of uncountable words."
  [& words]
  (doseq [word words] (add-uncountable-word word)))


