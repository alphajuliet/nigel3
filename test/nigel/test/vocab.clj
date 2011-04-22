(ns nigel.test.vocab
	(:use nigel.core :reload)
	(:use midje.sweet)
	(:use clojure.test)
	(:require [nigel.vocab :as vocab]))

;-------------------------
(defn my-fixture [f]
	(do
		(def adjs (vocab/get-vocab :Adjective))
		(def nouns (vocab/get-vocab :Noun))
	(f)))

(use-fixtures :each my-fixture)

;-------------------------
;Vocab loading
(deftest test-vocab
	(facts "vocab"
		(count adjs) => 822
		(:text (first adjs)) => "american"
		(:order (first adjs)) => "3"
		(:text (first nouns)) => "DJ"
		(:type (first nouns)) => "person"
;		(> 0 (:order (vocab/random :Adjective))) => true 
))
