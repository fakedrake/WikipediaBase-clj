(ns wikipediabase.core-test
  (:require [clojure.test :refer :all]
            [wikipediabase.core :refer :all]))

(deftest run-code-test
  (testing "Wikipediabase simple errorless code."
    (is (= (wikipediabase "(upone 1)") 2))))
