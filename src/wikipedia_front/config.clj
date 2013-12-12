(ns wikipedia-front.config
  (:require [wikipedia-front.functions]
            [wikipedia-front.api :as api])
  (:use [clojure.pprint :only [pprint]]))

(defn- parser-sym [sym]
  (ns-resolve 'wikipedia-front.parser sym))

(defn open-paren-ptok
  "Return a list where the first element is this list and the rest is
  the rest of the tokens."
  [toks]
  (if (empty? toks) (throw (Exception. "Unmatched parens.")))

  (let [tokls ((parser-sym 'pp-car) toks)
        car (first tokls)]

    (if (not= car 'close-paren-ptok)
      (let [parlist (open-paren-ptok (rest tokls))]
        (cons (cons car (first parlist)) (rest parlist)))
      (cons nil (rest tokls)))))

(defn close-paren-ptok
  [toks]
  (cons 'close-paren-ptok toks))

;; Preprocessing token map these functions should act on the list of
;; tokens after them and return that string with the effects of the
;; preprocessor token (usually that is just the changed)
(def ptokenmap {"(" open-paren-ptok
                ")" close-paren-ptok})

;; Literal Parsing

(api/wb-defl ^{:regex #"\d+"} dec-lit
             [l]
             (Integer. l))

(api/wb-defl ^{:regex #"0x[0-9a-fA-F]+"} hex-lit
             [l]
             (. Integer (parseInt (subs l 2) 16)))

(api/wb-defl ^{:regex #"(\d*\.\d+\|\d+\.\d*)"} float-lit
             [l]
             (Float. l))


(api/wb-defl ^{:regex #""} string-lit
             [l]
             l)

(api/wb-defl ^{:regex #":\w+"} keyword-lit
             [l]
             (keyword (subs l 1)))
