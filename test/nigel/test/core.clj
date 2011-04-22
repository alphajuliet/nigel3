(ns nigel.test.core
	(:use nigel.core :reload)
	(:use midje.sweet)
	(:use clojure.test)
	(:require [nigel.vocab :as vocab]))
;   (:require [clojure.contrib.string :as string]))

;-------------------------
(defn my-fixture [f]
	(do
		(def n1 (noun "cat"))
		(def n2 (noun "cats" :plural))
		(def n3 (noun "lass" :singular))
		(def n4 (noun "owner" :singular :person))
		(def n5 (noun "mat"))
		(def d1 (art :indefinite))
		(def d2 (art :definite))
		(def d3 (art :indefinite :plural))
		(def adj1 (adj "black" 2))
		(def adj2 (adj "large" 1))
		(def p1 (prep "on"))
		(def p2 (prep "under"))
		(def np1 (np d1 adj1 n1))
		(def np2 (np d1 adj1 adj2 n1))
		(def np3 (np d1 adj1 adj2 n1 n4))
		(def pp1 (pp p1 np1))
		(def pp2 (pp p1 d2 n5))
		(def v1 (verb "walk" 1))
		(def v2 (verb "go" 1 {:imperfect "went", :past-part "gone"}))
		(def v3 (verb "be" 1 {:imperfect "were", :past-part "been"}))
		(def c1 (clause np1 (make-plural v1)))
	(f)))

(use-fixtures :each my-fixture)

;-------------------------
;Are we awake?
(deftest sanity
	(fact (+ 2 2) => 4))

;-------------------------
;Noun
(deftest test-noun1
	(facts "nouns"
		(text n1) => "cat"
		(text n2) => "cats"
		(text (make-plural n1)) => "cats"
		(make-plural n2) => n2
		(text (make-plural n3)) => "lasses"))

;-------------------------
;Article
(deftest test-arterminer
	(facts "determiners"
		(text d1) => "a"
		(text d2) => "the"
		(text d3) => ""
		(make-plural d1) => (art :indefinite :plural)))

;-------------------------
;Adjective
(deftest test-adj
	(facts "adjectives"
		(text adj1) => "black"))

;-------------------------
;Preposition
(deftest test-prep
	(facts "prepositions"
		(text p1) => "on"))
		
;-------------------------
;Noun phrase (NP)
(deftest test-np
	(facts "noun phrases"
		(text np1) => "a black cat"
		(text np2) => "a large black cat"
		(text np3) => "a large black cat owner"
		(text (make-plural np1)) => "black cats"
))

;-------------------------
;Prepositional phrase (PP)
(deftest test-pp
	(facts "prepositional phrase"
		(text pp1) => "on a black cat"
		(text (make-plural pp1)) => "on black cats"))

;-------------------------
;Verbs
(deftest test-verb
	(facts "verbs"
		(text v1) => "walk"
		(text (make-plural v1)) => "walks"
		(make-past-part "end") => "ended"
		(text (change-tense :imperfect v1)) => "walked"
		(text (change-tense :imperfect v2)) => "went"
		(:person (change-person :sing3 v1)) => :sing3
		(text (change-person :sing3 v1)) => "walks"
		(text (change-person :plural v1)) => "walk"
		(text (change-person :sing1 v3)) => "am"
))

;-------------------------
;Clauses
(deftest test-clause
	(facts "clauses"
		(:base c1) => :Clause
		(text c1) => "a black cat walks"
))

