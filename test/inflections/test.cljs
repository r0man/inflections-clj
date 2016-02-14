(ns inflections.test
  (:require [inflections.core-test]
            [doo.runner :refer-macros [doo-tests]]))

(doo-tests 'inflections.core-test)
