(ns war.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [war.core-test]))

(doo-tests 'war.core-test)
