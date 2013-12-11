(ns wikipedia-front.core-test
  (:require [clojure.test :refer :all]
            [wikipedia-front.core :refer :all]))

(deftest run-code-test
  (testing "Wikipediabase simple errorless code."
    (is (= (wikipediabase "(upone 1)") 2))))
