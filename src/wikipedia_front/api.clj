(ns wikipedia-front.api
  (:use [clojure.pprint :only [pprint]]))

(def wb-functions (atom {}))
(def wb-literals (atom '()))

(defmacro wb-throw
  "Throw an exception. You can user the string like format."
  [fmt & args]
  `(throw (Exception. (format ~fmt ~@args))))

(defmacro defn-sym
  "Create a function and get the symbol."
  [name fdecls]
  `(var-get (eval (macroexpand (list* `defn ~name ~fdecls)))))

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

;; I think this is returning a defn that returns my defn not the defn
;; itself.
(defmacro wb-defl
  "Define a way to parse literals. You will need to define ^{:regexp
  <literal regex>}"
  [name & fdecls]
  (let [fsym (defn-sym name fdecls)
        rx (or (:regex (meta name))
               (wb-throw "Undefined regexp for literal type `%s'." (str name)))]
    (when (not= (type #"s") java.util.regex.Pattern)
      (wb-throw "Literal type `%s' is has not a valid regexp."))

    (reset! wikipedia-front.api/wb-literals
            (cons [rx fsym] @wikipedia-front.api/wb-literals))
    fsym))
