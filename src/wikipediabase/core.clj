(ns wikipediabase.core
  (:require [clojure.string :as str]
            [wikipediabase.parser :as parser])
  (:use [clojure.pprint :only [pprint]]))

(defn wikipediabase
  "Actually run the code you receive as a string"
  [inp]
  ;; XXX: have an evaluator do this job, it is unsafe.
  (eval (parser/pp inp)))
