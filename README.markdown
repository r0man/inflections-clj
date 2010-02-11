# Clojure Inflection Library

Rails-like inflections for Clojure ...

Examples:
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
user> 
user> (underscore "puni-puni")
"puni_puni"
user> (uncountable? "sheep")
true
</code>
</pre>


