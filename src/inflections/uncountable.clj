(ns inflections.uncountable
  (:use inflections.helper))

(def *uncountable-words* (atom (set [])))

(defn add-uncountable
  "Adds a the given word to the list of uncountable words."
  [word]
  (swap! *uncountable-words* conj (normalize-word word)))

(defn delete-uncountable
  "Deletes the given word from the list of uncountable words."
  [word]
  (swap! *uncountable-words* disj (normalize-word word)))

(defn uncountable?
  "Returns true if the given word is uncountable, else false."
  [word]
  (contains? @*uncountable-words* (normalize-word word)))

(defn uncountable!
  "Adds the given word to the list of uncountable words."
  [word]
  (add-uncountable word))

(defmacro uncountable [& words]
  (doseq [word words] (uncountable! word)))

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
