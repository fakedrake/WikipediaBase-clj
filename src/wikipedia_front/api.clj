(ns wikipedia-front.api
  (:use [clojure.pprint :only [pprint]]))

(def wb-functions (atom {}))

(defmacro defn-sym
  "Create a function and get the symbol."
  [name fdecls]
  `(eval (macroexpand (list* `defn ~name ~fdecls))))

(defmacro wb-defn
  "Defines a function and informs wb-functions about it. You may use
  ^{:cmd-name <name>} to define the name of the function in the
  interface. Use this to avoid namespace conflicts."
  [name & fdecls]
  (let [fsym (defn-sym name fdecls)]
    (reset! wikipedia-front.api/wb-functions
            (assoc @wikipedia-front.api/wb-functions
              (or (:cmd-name (meta name)) (str name)) fsym))
    fsym))
