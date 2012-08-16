;------------------------------
; Nigel3 = Nigel Migraine in Clojure
; AndrewJ
; created: 2010-12
;------------------------------

(ns nigel.core
  (:require [nigel.vocab :as vocab]
            [clojure.string]))

;-------------------------
; Multimethod definitions
(defmulti text :base)
(defmulti make-plural :base)
(defmulti make-random :base)

;-------------------------
;Default implementations
(defmethod text :default [x] (:text x))
(defmethod make-plural :default [x] x)
(defmethod make-random :default [x] nil)

;-------------------------
;Utility functions
(defn textify [coll]
  (clojure.string/triml (clojure.string/join " " (map text coll))))

; Copied from clojure.contrib.string 1.2.0
(defn ^String tail
  "Returns the last n characters of s."
  [n ^String s]
  (if (< (count s) n)
    s
    (.substring s (- (count s) n))))

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
    (= "ss" (tail 2 (text n))) (noun (str (:text n) "es") :plural)
    true (noun (str (text n) "s") :plural)))

(defmethod make-random :Noun [n]
  (vocab/random :Noun))

;-------------------------
;Article

(defn art
  ([type] {:base :Art :type type :count :singular :vowel-next false})
  ([type count] {:base :Art :type type :count count :vowel-next false}))

(defn match-article [art next-word]
  "Change 'a' to 'an' if followed by a vowel"
  (if (and
        (= (:type art) :indefinite)
        (re-find #"^[aeiou]" (:text next-word)))
    (assoc art :vowel-next true)
    art))

(defmethod text :Art [a]
  (cond
    (= :definite (:type a)) "the"
    (= :plural (:count a)) ""
    (:vowel-next a) "an"
    true "a"))

(defmethod make-plural :Art [a]
  (if (not= :plural (:count a)) 
    (art (:type a) :plural)
    a))

(defmethod make-random :Art [a]
  (art (rand-nth [:definite :indefinite])))

;-------------------------
;Adjective

(defn adj
  ([word] {:base :Adjective, :text word, :order 99})
  ([word order] {:base :Adjective, :text word, :order order}))

;@todo Work out how to use derive
;(derive :Adjective :X)
(defmethod text :Adjective [a] (:text a))
(defmethod make-plural :Adjective [a] a)
(defmethod make-random :Adjective [a]
  (vocab/random :Adjective))

;-------------------------
;Noun phrase

;Return a list of NP elts sorted by an adjective's order. All other elements remain unchanged.
(defn sort-elts [elts]
  (let [x (sort 
            #(if (and 
                   (= :Adjective (:base %1)) 
                   (= :Adjective (:base %2))) 
               (< (:order %1) (:order %2)) 
               0)
            elts)]
    x))
			
(defn np [& coll] 
  {:base :NP, :elts (flatten (sort-elts coll))})

(defmethod text :NP [x]
  (textify (:elts x)))
	
(defmethod make-plural :NP [np]
  {:base :NP, :elts (map make-plural (:elts np))})
;(np (map make-plural (:elts np))))  ; NOTE <-- doesn't work.  I don't understand why.
;@todo Only pluralise the *last* noun in an NP, e.g. cat owner -> cat owners
;@todo Match a/an article with next item

(defmethod make-random :NP [x]
  (np 
    (make-random {:base :Art})
    (make-random {:base :Adjective})
    (make-random {:base :Noun})))

;-------------------------
;Preposition

(defn prep [word] 
  {:base :Prep, :text word})
(defmethod text :Prep [p] 
  (:text p))
(defmethod make-plural :Prep [p] p)

;-------------------------
;Prepositional phrase

(defn prep-phrase [prep np] 
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
    (= "ss" (tail 2 (text v))) (verb (str (:text v) "es"))
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

(defmethod make-random :Verb [v]
  (vocab/random :Verb))

;-------------------------
; Clause

(defn clause [ & coll ] 
  {:base :Clause, :elts coll})
  ;@todo Enforce matching of number between subject and verb, e.g. "the cat sits"	

(defmethod text :Clause [c] (textify (:elts c)))
	
;-------------------------
; Go!

(defn random-np []
  (text (make-random {:base :NP})))


(defn -main [& args]
  (do
    (println "Main...")
    (println (random-np))))
	
;The End 
