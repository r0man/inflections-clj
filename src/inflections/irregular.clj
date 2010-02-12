(ns inflections.irregular
  (:use clojure.contrib.seq-utils
        inflections.helper
        inflections.plural
        inflections.singular
        inflections.uncountable))

(def *irregular-words* (atom (sorted-set)))

(defn add-irregular-word
  "Adds a the given word to the list of irregular words."
  [word]
  (swap! *irregular-words* conj (normalize-word word)))

(defn delete-irregular-word
  "Deletes the given word from the list of irregular words."
  [word]
  (swap! *irregular-words* disj (normalize-word word)))

(defn reset-irregular-words
  "Resets the set of irregular words."
  []
  (reset! *irregular-words* (sorted-set)))

(defn irregular?
  "Returns true if the given word is irregular, else false."
  [word]
  (contains? @*irregular-words* (normalize-word word)))

(defn irregular!
  "Define words that are irregular in singular and plural."
  [& singulars-and-plurals]
  (assert-even-args singulars-and-plurals)
  (doseq [[singular plural] (partition 2 singulars-and-plurals)]
    (delete-uncountable-word singular)
    (delete-uncountable-word plural)
    (singular! plural singular)
    (plural! singular plural)
    (add-irregular-word singular)
    (add-irregular-word plural)))

(defn init-irregular-words []
  (reset-irregular-words)
  (irregular!
   "child" "children"
   "cow" "kine"
   "foot" "feet"
   "louse" "lice"
   "mailman" "mailmen"
   "man" "men"
   "mouse" "mice"
   "move" "moves"
   "ox" "oxen"
   "person" "people"
   "sex" "sexes"
   "tooth" "teeth"
   "woman" "women"))
