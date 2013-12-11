(ns wikipedia-front.parser
  (:require [clojure.string :as str]
            [wikipedia-front.config :as cfg]
            [wikipedia-front.api :as api]))

(declare parse-token)

(defn pp-car
  "Preprocessor: Return a list of tokens only with the car
  parsed. :tok-parse-override is called on the car of toks instead of
  `parse-token' if you provide it and :tok-parse-fallback is send to
  `parse-token' as :fallback. Note that these methods should make no
  assumptions on what they will receive as a token."
  [toks & {:keys [tok-parse-override
                  tok-parse-fallback]}]
  (if (and (coll? toks) (contains? cfg/ptokenmap (first toks)))
    ((cfg/ptokenmap (first toks)) (rest toks))
    ;; Try overriding or call parse-token with the fallback
    (cons (or (when tok-parse-override
                (tok-parse-override (first toks)))
              (parse-token (first toks) :fallback (or tok-parse-fallback nil)))
          (rest toks))))

;; I was not very comfortable with treating strings as special
;; literals and processing them before the tokenizer but they are the
;; only literal that affects the tokenizer.
(defn tokenize
  "Tokenizes taking into consideration strings. If a quoted string is
  encountered we output '(string-lit <the strig>) instead of just <the
  string>"
  [exp]
  (remove empty?
          (mapcat (fn [[e s]]
                    (concat (str/split (reduce #(str/replace %1 %2 (str " " %2 " "))
                                               (cons e (keys cfg/ptokenmap)))
                                       #"\s+") [(if s (list cfg/string-lit s) "")]))
                  (partition 2 2 [nil] (str/split exp #"\"")))))

(defn pp
  "This is the preprocessor. Given a string it returns a structure of
  lists."
  [in]
  (let [res (-> in tokenize pp-car)]
    (if (empty? (rest res))
      (first res)
      (throw (Exception. (format "More than one expression in `%s'." in))))))

;; Dont return nil on failure, we might need it for a literal.
(defn parse-literal
  "Parse literal based on the `literal-regex-list'. Will return
  'non-literal if it cant be resolved. Non strings are always
  considered literals."
  [lit]
  (if (string? lit)
    (let [lt-res (some #(when (re-matches (first %) lit) (second %))
                       cfg/literal-regex-list)]
      (if (fn? lt-res) (list lt-res lit)
          (or lt-res 'non-literal)))
    lit))

(defn parse-token
  "Parse a symbol found in the `symbol-map'. If you cant seem to parse
  it use fallback or raise an exception."
  [tok & {:keys [fallback]}]
  (let [lit (parse-literal tok)]
    (cond
     (not= lit 'non-literal) (eval lit)
     (contains? @api/wb-functions tok) (@api/wb-functions tok)
     :default (if fallback (fallback tok)
                  (throw (Exception. (format "Could not resolve '%s'" tok)))))))
