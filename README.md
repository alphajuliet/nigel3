# nigel

Nigel Migraine is a bottom-up natural language generator (NLG) written in Clojure, as a learning exercise. It has been rewritten in a number of languages over time (Perl, Java, Ruby) as a way of learning the language. I'm not a linguist, merely an amateur.

This is a permanent work in progress.

## Usage


## Installation


## License

Copyright (C) 2012 Andrew Joyner
Distributed under the Eclipse Public License, the same as Clojure.

# Notes on nigel3 structures
	
## Noun

	(noun word)

	(text (noun {} "cat")) => "cat"
	(text (noun "cat")) => "cat"
	(text (noun "cats" :plural)) => "cats"
	(make-plural (noun "cat")) => (noun "cats" :plural)
	(make-plural (noun "lass")) => (noun "lasses" plural)

## Determiners

	(art word)
	
	(text (art :indefinite)) => "a"
	(text (art :definite)) => "the"
	(text (art :indefinite :singular)) => "a"
	(text (art :indefinite :plural)) => ""
	(make-plural (art :indefinite)) => (art :indefinite :plural)

## Adjective

	(adj word)

	(text (adj "black")) => "black"
	(text (adj "black" 2)) => "black"

## Noun Phrase

	(np art adj noun)
	(np art adj noun 
	elts = art? adj* noun+

	(text (np 
		(art :indefinite :singular) 
		(adj "black" 2)
		(noun "cat" :singular))) => "a black cat"
	
	(text (np
		(art :indefinite) 
		(adj "black" 2)
		(adj "large" 1)
		(noun "cat"))) => "a large black cat"
	
	(text (make-plural (np 
		(art :indefinite) 
		(adj "black" 2)
		(noun "cat")))) => "black cats"
	
	(text(np
		(art :indefinite) 
		(adj "black" 2)
		(adj "large" 1)
		(noun "cat")
		(noun "owner"))) => "a large black cat owner"
		
## Verb

	(text (verb "go")) => "go"
	(text (verb "go" "went" "gone")) => "go"
	(make-plural (verb "go")) => "go"  (e.g. they go)
	(change-tense :perfect (verb "go" "went" "gone")) => "have gone"
	
### Tenses ###
	
	:imperfect -> I walked
	:perfect -> I have walked
	:pluperfect -> I had walked
	:future -> I will walk
	:conditional -> I would walk
	:future perfect -> I will have walked
	:conditional perfect -> I would have walked
	
### Voices ###
	
	:active -> I give the book to him
	:passive -> the book is given by me
	
### Conjugations ###

	:sing1 -> 1st person singular ("to be" -> am)
	:sing3 -> 3rd person singular
	:plural -> all plurals

## Verb Phrase

	(vp verb dir-object indir-object)

## Preposition

	(prep word)
	
## Prepositional Phrase

	(phrase prep np)

## References

* [Penn Treebank tag set](http://www.ims.uni-stuttgart.de/projekte/CorpusWorkbench/CQP-HTMLDemo/PennTreebankTS.html)

## XML processing

### Adjective ###

	({:tag :Adjective, :attrs {:order "3"}, :content ["american"]}
	 {:tag :Adjective, :attrs {:order "5"}, :content ["communist"]}
	 {:tag :Adjective, :attrs {:order "5"}, :content ["Freudian"]}
	 {:tag :Adjective, :attrs {:order "3"}, :content ["gothic"]}
	 {:tag :Adjective, :attrs {:order "5"}, :content ["Oedipal"]}
	 {:tag :Adjective, :attrs {:order "5"}, :content ["olympic"]}
	 ...) 
 
 Transform
 
 	{:tag :Adjective :attrs {:order o} :content [t]} ==> (:base Adjective :order o :text t)

### Verb ###

	(load-vocab :Verb) -> 
		{:tag :Verb, 
			:attrs {:object-type 6}, 
			:content [yank 
				{:tag :DirectObject, 
					:attrs {:noun_type thing}, 
					:content nil} 
				{:tag :IndirectObject, 
					:attrs {:preposition into, :noun_type place}, 
					:content nil}]})

	(get-vocab :Verb) -> {:base :Verb, :text yank, :object-type 6}
	

