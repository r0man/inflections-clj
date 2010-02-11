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

Copyright (c) 2010 Roman Scherer. All rights reserved. The use and
distribution terms for this software are covered by the Eclipse Public
License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which can
be found in the file COPYING at the root of this distribution. By
using this software in any fashion, you are agreeing to be bound by
the terms of this license.  You must not remove this notice, or any
other, from this software.
