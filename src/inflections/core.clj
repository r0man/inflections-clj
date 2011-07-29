(ns #^{:author "Roman Scherer"
       :doc "Rails-like inflections for Clojure.

Examples:
user> (use 'inflections)
nil
user> (pluralize \"word\")
\"words\"
user> (pluralize \"virus\")
\"viri\"
user> (singularize \"apples\")
\"apple\"
user> (singularize \"octopi\")
\"octopus\"
user> (underscore \"puni-puni\")
\"puni_puni\"
user> (ordinalize \"52\")
\"52nd\"
user> (capitalize \"clojure\")
\"Clojure\"
"}
  inflections.core)

(defn- immigrate
  "Create a public var in this namespace for each public var in the
  namespaces named by ns-names. The created vars have the same name, root
  binding, and metadata as the original except that their :ns metadata
  value is this namespace."
  [& ns-names]
  (doseq [ns ns-names]
    (require ns)
    (doseq [[sym var] (ns-publics ns)]
      (let [sym (with-meta sym (assoc (meta var) :ns *ns*))]
        (if (.hasRoot var)
          (intern *ns* sym (.getRawRoot var))
          (intern *ns* sym))))))

(immigrate
 'inflections.irregular
 'inflections.plural
 'inflections.singular
 'inflections.transform
 'inflections.uncountable)

(defn init-inflections []
  (init-plural-rules)
  (init-singular-rules)
  (init-uncountable-words)
  (init-irregular-words))

(init-inflections)
