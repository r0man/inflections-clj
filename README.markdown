# Clojure Inflection Library

Rails-like inflections for Clojure.

## Installation

The JAR is available on [Clojars](http://clojars.org/inflections), or use
[Leiningen](http://github.com/technomancy/leiningen) to build your own
...

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
user> (underscore "puni-puni")
"puni_puni"
user> (ordinalize "52")
"52nd"
user> (capitalize "clojure")
"Clojure"
...
</code>
</pre>

### License

Copyright (C) 2010 Roman Scherer.

Distributed under the Eclipse Public License, the same as Clojure
uses. See the file COPYING for more information.

