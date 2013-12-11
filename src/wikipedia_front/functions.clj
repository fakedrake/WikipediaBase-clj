(ns wikipedia-front.functions
  (:require [wikipedia-front.api :as api]))

(api/wb-defn upone
         "Increment"
         [x]
         (inc x))
