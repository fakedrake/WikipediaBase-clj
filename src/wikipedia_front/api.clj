(ns wikipedia-front.api
  (:use [clojure.pprint :only [pprint]]))

(def wb-functions (atom {}))

(defmacro wb-defn
  "Defines a function and informs wb-functions about it. You may use
  ^{:cmd-name <name>} to define the name of the function in the
  interface. Use this to avoid namespace conflicts."
  [name & fdecls]

  (let [fsym (eval (macroexpand (list* `defn name fdecls)))]
    (reset! wikipedia-front.api/wb-functions
            (assoc @wikipedia-front.api/wb-functions
              (or (:cmd-name (meta name)) (str name)) fsym))
  fsym))
