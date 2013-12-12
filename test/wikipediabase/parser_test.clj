(ns wikipediabase.parser-test
  (:require [clojure.test :refer :all]
            [wikipediabase.parser :refer :all]
            [wikipediabase.config :as cfg]))

(deftest tokenize-test
  (testing "Normal tokenizer use"
    (is (= (tokenize "(a b (c d) e)")
           '("(" "a" "b" "(" "c" "d" ")" "e" ")"))))

  (testing "Unbalanced parentheses"
    (is (thrown? Exception (pp-car '("(" ":a" ":b"))))))


(deftest car-preprocessor-test
  (testing "Preprocessor"
    (is (= (pp-car '("(" ":a" ":b" "(" ":c" ":d" ")" ":e" ")" "f" "g" ")"))
           '((:a :b (:c :d) :e) "f" "g" ")")))))

(deftest parse-tokens
  (testing "Smallchunk parsing"
    (is (= (parse-literal ":a") (list cfg/keyword-lit ":a")))
    (is (= (parse-token ":a") :a))))
