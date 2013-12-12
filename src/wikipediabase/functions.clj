(ns wikipediabase.functions
  (:require [wikipediabase.api :as api]))

(api/wb-defn upone
         "Increment"
         [x]
         (inc x))
