# inflections
  [![Build Status](https://travis-ci.org/r0man/inflections-clj.svg)](https://travis-ci.org/r0man/inflections-clj)
  [![Dependencies Status](http://jarkeeper.com/r0man/inflections-clj/status.svg)](http://jarkeeper.com/r0man/inflections-clj)

Rails-like inflection library for Clojure and ClojureScript.

![](https://farm3.staticflickr.com/2827/12305713514_d3b3f53721_m_d.jpg)

## Installation

Via Clojars: http://clojars.org/inflections.

[![Current Version](https://clojars.org/inflections/latest-version.svg)](https://clojars.org/inflections)

## Usage

``` clj
(use 'inflections.core)

(plural "word")
;=> "words"

(plural "virus")
;=> "viri"

(pluralize 12 "virus")
;=> "12 viri"

(singular "apples")
;=> "apple"

(singular "octopi")
;=> "octopus"

(underscore "puni-puni")
;=> "puni_puni"

(ordinalize "52")
;=> "52nd"

(capitalize "clojure")
;=> "Clojure"
```

## License

Copyright (C) 2013-2016 r0man

Distributed under the Eclipse Public License, the same as Clojure.
