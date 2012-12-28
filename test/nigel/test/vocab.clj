(ns nigel.test.vocab
  (:use [nigel.vocab]
        [midje.sweet]))

;-------------------------
(facts "Check vocab retrieved"
       (let [adjs (get-vocab :Adjective)
             nouns (get-vocab :Noun)
             verbs (get-vocab :Verb)]
         (count adjs) => 822
         (:text (first adjs)) => "american"
         (:order (first adjs)) => "3"
         (:text (first nouns)) => "DJ"
         (:type (first nouns)) => "person"
         (:text (first verbs)) => "accompany"))

;(> 0 (:order (vocab/random :Adjective))) => true 
; The End
