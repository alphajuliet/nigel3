;------------------------------
; Nigel3 = Nigel Migraine in Clojure
; AndrewJ
; created: 2010-12
;------------------------------

(ns nigel.vocab
	(:use [clojure.xml :only (parse)])
	(:require [clojure.contrib.string :as string]))

;Relaxed string to integer conversion
(defn parse-integer [str]
    (try (Integer/parseInt str) 
         (catch NumberFormatException nfe str)))

;------------------------------
;Locations of vocab files
(def source-file
	{:Adjective "words/Adjective.xml"
	:Noun "words/Noun.xml"
	:Verb "words/Verb.xml"
	:Adverb "words/Adverb.xml"})	
	
;------------------------------
;Read a vocab file
(defn load-vocab [tag-name]
	(for [x (xml-seq 
		(parse (java.io.File. (tag-name source-file))))
        :when (= tag-name (:tag x))]
	x))

;Transform raw vocab file into Nigel form
;@todo Convert numeric string attributes to integers
(defn get-vocab [tag-name]
	(for [e (load-vocab tag-name)]
		(into {:base tag-name :text (string/trim (first (:content e)))} (:attrs e))))

;Find a random word
(defn random [tag-name]
	(rand-nth (get-vocab tag-name)))
	
;(println (load-vocab :Verb))

;The End
