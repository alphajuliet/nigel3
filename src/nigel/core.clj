;------------------------------
; Nigel3 = Nigel Migraine in Clojure
; AndrewJ
; created: 2010-12
;------------------------------

(ns nigel.core
  (:require [nigel.vocab :as vocab]
            [clojure.contrib.string :as string]))

;-------------------------
; Multimethod definitions
(defmulti text :base)
(defmulti make-plural :base)

;-------------------------
;Default implementations
(defmethod text :default [x] (:text x))
(defmethod make-plural :default [x] x)

;-------------------------
;Utility functions
(defn textify [coll]
  (string/ltrim (string/join " " (map text coll))))

;-------------------------
;Noun

(defn noun
  ([word] {:base :Noun, :text word, :count :singular, :type :thing})
  ([word count] 
    (assoc (noun word) :count count))
  ([word count type] 
    (assoc (noun word count) :type type)))

(defmethod text :Noun [n] (:text n))
	
(defmethod make-plural :Noun [n]
  (cond
    (= :plural (:count n)) n  ;if already plural 
    (= "ss" (string/tail 2 (text n))) (noun (str (:text n) "es") :plural)
    true (noun (str (text n) "s") :plural)))

;-------------------------
;Article

(defn art
  ([type] {:base :Art :type type :count :singular})
  ([type count] {:base :Art :type type :count count}))

(defmethod text :Art [a]
  (cond
    (= :definite (:type a)) "the"
    (and 
      (= :plural (:count a)) 
      (not= :definite (:type a))) 
    ""
    true "a"))

(defmethod make-plural :Art [a]
  (cond
    (not= :plural (:count a)) (art (:type a) :plural)
    true a))
	
;-------------------------
;Adjective

(defn adj
  ([word] {:base :Adjective, :text word, :order 99})
  ([word order] {:base :Adjective, :text word, :order order}))

;@todo Work out how to use derive
;(derive :Adjective :X)
(defmethod text :Adjective [a] (:text a))
(defmethod make-plural :Adjective[a] a)

;-------------------------
;Noun phrase

;Return a list of NP elts sorted by an adjective's order. All other elements remain unchanged.
(defn sort-elts [elts]
  (let [x (sort 
            #(if (and (= :Adjective (:base %1)) (= :Adjective (:base %2))) 
               (< (:order %1) (:order %2)) 
               0)
            elts)]
    x))
			
(defn np [& coll] {:base :NP, :elts (flatten (sort-elts coll))})

(defmethod text :NP [np]
  (textify (:elts np)))
	
(defmethod make-plural :NP [np]
  {:base :NP, :elts (map make-plural (:elts np))})
;(np (map make-plural (:elts np))))  ; NOTE <-- doesn't work.  I don't understand why.
;@todo Only pluralise the *last* noun in an NP, e.g. cat owner -> cat owners
;@todo Generate random noun-phrases

;-------------------------
;Preposition

(defn prep [word] 
  {:base :Prep, :text word})
(defmethod text :Prep [p] 
  (:text p))
(defmethod make-plural :Prep [p] p)

;-------------------------
;Prepositional phrase

(defn pp [prep np] 
  {:base :PP, :elts (list prep np)})
(defmethod text :PP [pp] 
  (textify (:elts pp)))
(defmethod make-plural :PP [pp] 
  {:base :PP, :elts (map make-plural (:elts pp))})

;-------------------------
;Verb	

(defn make-past-part [s]
  (str s "ed"))

(defn verb
  ([word] {:base :Verb, :text word, :person :plural, :tense :present, 
           :conjugates {:imperfect (make-past-part word), :past-part (make-past-part word)}})
  ([word type] 
    (assoc (verb word) :object-type type))
  ([word type conjs] 
    (assoc (verb word type) :conjugates {:imperfect (:imperfect conjs), :past-part (:past-part conjs) })))

(defmethod text :Verb [v] (:text v))

(defmethod make-plural :Verb [v]
  (cond
    ;(= :plural (:person v)) v  ;if already plural 
    (= "ss" (string/tail 2 (text v))) (verb (str (:text v) "es"))
    true (verb (str (text v) "s"))))

(defn change-tense [t v]
  (let [c (:conjugates v)]
    (verb (t c) (:type v) {:imperfect c, :past-part c})))	
	;@todo Handle compound tenses, i.e perfect, pluperfect, future perfect, conditional...
		
(defn change-person [p v]
  (cond
    (= p (:person v)) v
    (= :sing3 p) 
    (assoc (make-plural v) :person p)
    (and (= :sing1 p) (= "be" (:text v))) 
    (assoc v :text "am" :person p)
    true v))

;-------------------------
; Clause

(defn clause [ & coll ] 
  {:base :Clause, :elts coll})
  ;@todo Enforce matching of number between subject and verb, e.g. "the cat sits"	

(defmethod text :Clause [c] (textify (:elts c)))
	
;-------------------------
; Go!

(defn random-clause []
  (let [n (vocab/random :Noun)
        v (vocab/random :Verb)]
    (text (clause n v))))


(defn -main [& args]
  (do
    (println "Main...")
    (println (random-clause))))
	
;The End 
