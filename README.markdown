# Clojure Inflection Library

Rails-like inflections for Clojure ...

Examples:
<pre>
<code>
user> (use 'inflections)
nil
user> (dasherize "puni_puni")
"puni-puni"
user> (underscore "puni-puni")
"puni_puni"
user> (underscore "puni puni")
"puni_puni"
user> (uncountable? "sheep")
true
user> (uncountable? "word")
false
</code>
</pre>


