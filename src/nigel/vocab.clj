;------------------------------
; Nigel3 = Nigel Migraine in Clojure
; AndrewJ
; created: 2010-12
;------------------------------
(ns nigel.vocab
  (:require [clojure.contrib.string :as string]
            [clojure.xml :as xml]))

;Relaxed string to integer conversion
(defn parse-integer [str]
  (try 
    (Integer/parseInt str) 
    (catch NumberFormatException nfe str)
    (catch ClassCastException nfe str)))

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
    (for [x (xml-seq (xml/parse (java.io.File. (source-file tag-name))))
          :when (= tag-name (:tag x))]
      x))
 
;Transform raw vocab file into Nigel form
;@todo Convert numeric string attributes to integers
(defn get-vocab [tag-name]
  (for [e (load-vocab tag-name)]
    (into 
      {:base tag-name :text (string/trim (first (:content e)))} 
      (map parse-integer (:attrs e)))))

;Find a random word
(defn random [tag-name]
  (rand-nth (get-vocab tag-name)))
	
;The End
