(ns inflections.irregular
  (:use clojure.contrib.seq-utils inflections.helper))

(def *irregular-words* (atom {}))

(defn irregular! [singular plural]
  (let [singular (normalize-word singular)]
    (if-not (@*irregular-words* singular)
      (swap! *irregular-words* assoc singular (normalize-word plural)))))

(defn irregular?
  "Returns true if the given word is irregular, else false."
  [word]
  (let [word (normalize-word word)]
    (or (includes? (keys @*irregular-words*) word)
        (includes? (vals @*irregular-words*) word))))

(defmacro irregular [& words]
  (if-not (= (mod (count words) 2) 0)
    (throw (IllegalArgumentException. "Odd number of words given. Pairs of singular and plural words expected.")))
  (doseq [[singular plural] (partition 2 words)]
    (irregular! singular plural)))

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
