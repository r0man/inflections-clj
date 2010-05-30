# Clojure Inflection Library

Rails-like inflections for Clojure.

### Installation

The JAR is available on [Clojars](http://clojars.org/inflections). The
master branch is targeting Clojure v1.2. Use versions below 0.4 for
previous versions.

### Documentation 

The API documentation is available [here](http://r0man.github.com/inflections-clj).

### Examples
<pre>
<code>
user> (use 'inflections)
nil
user> (pluralize "word")
"words"
user> (pluralize "virus")
"viri"
user> (singularize "apples")
"apple"
user> (singularize "octopi")
"octopus"
user> (underscore "puni-puni")
"puni_puni"
user> (ordinalize "52")
"52nd"
user> (capitalize "clojure")
"Clojure"
</code>
</pre>
