(define (domain ia_project)
 (:requirements :strips :typing :negative-preconditions :disjunctive-preconditions)
 (:types
  location      ;  there are several connected locations
  person        ;  Each injured person is at a specific location
  box           ;  Each box is initially at a specific location and can be filled with a specific content
  content  
  type
  space
  carrier
  robot
  
  )
  
  


(:predicates

   
    
    (carr_space ?c - carrier ?s - space)  ;space s in carrier c is occupied
    
    (pos_robot ?r - robot ?l - location) ;robot r is in location l
    (pos_person ?p - person ?l - location) ; person p is in location l
    (pos_box ?b - box ?l - location) ; box b is in location l
    
    (pos_content ?ct - content ?l - location) ; content ct is in location l
    (pos_carr ?c - carrier ?l - location) ; carrier c is in location l
    


    (type_of_content ?ct - content ?t - type) ; content ct is type t

    (needs_content ?p - person ?t - type) ; person p need content of type t
    (needs_or_content ?p -person ?t1 - type ?t2 - type) ; person p need content of type t1 or t2

    ;box
    (empty ?b - box) ; box b is empty 
    (contains_content ?b - box ?ct - content ) ; box b contains content ct
    
    ;carrier
    (loaded_on ?b - box ?c - carrier)     ; box b is loaded on carrier c
    
    
    
    
)


;; robot r moves from location from to location to
 (:action move_robot
     :parameters (?r - robot ?from ?to - location) 
     :precondition (and (pos_robot ?r ?from)
                    )
     
     :effect (and (pos_robot ?r ?to)
                (not (pos_robot ?r ?from)) ))
                  
                  
;; robot r move carrier c from location from to location to
 (:action move_carrier
     :parameters (?c - carrier ?from ?to - location ?r - robot)  ;
     :precondition (and (pos_carr ?c ?from)
                        (pos_robot ?r ?from)
                        )
     
     :effect (and (pos_carr ?c ?to) (not (pos_carr ?c ?from)) 
                  (pos_robot ?r ?to) (not (pos_robot ?r ?from)) 
                  ))
                  
                  
                  
                  
; robot r load a box b on carrier c
 (:action load_carrier_with_box
     :parameters (?c - carrier ?l - location ?b - box ?r - robot ?s - space)
     :precondition (and (pos_carr ?c ?l) 
                        (pos_box ?b ?l)
                        (pos_robot ?r ?l)
                        (carr_space ?c ?s)
                        )
                        
     :effect (and (loaded_on ?b ?c)
                  (not (pos_box ?b ?l))
                  (not (carr_space ?c ?s))
            )
)

; robot r unload box b from carrier c
 (:action unload_carrier_with_box
     :parameters (?c - carrier ?l - location ?b - box ?r - robot ?s - space)
     :precondition (and (pos_carr ?c ?l) 
                        (pos_robot ?r ?l)
                        (loaded_on ?b ?c)
                        )
                        
     :effect (and (not (loaded_on ?b ?c))
                  (carr_space ?c ?s)
                  (pos_box ?b ?l)
            )
)



; robot r fill a empty box b on carrier c with content ct
 (:action fill_empty_box
     :parameters (?c - carrier ?l - location ?ct - content  ?r - robot  ?b - box)
     :precondition (and (pos_carr ?c ?l) 
                        (pos_content ?ct ?l)
                        (pos_robot ?r ?l)
                        (loaded_on ?b ?c)
                        (empty ?b)
                    )
                        
     :effect (and (contains_content ?b ?ct)
                  (not (pos_content ?ct ?l))
                  (not (empty ?b))
            )
)
            
; robot r unload content ct from box b loadend on carrier c to the location l
(:action unload_content
 :parameters (?c - carrier ?l - location ?b - box ?r - robot ?p - person ?ct - content ?t1 - type ?t2 - type)
 :precondition (and (pos_carr ?c ?l) 
                    (pos_robot ?r ?l)
                    (pos_person ?p ?l)
                    (loaded_on ?b ?c)
                    (not (empty ?b))
                    (contains_content ?b ?ct)
                    (or
                        (needs_content ?p ?t1)
                        (needs_or_content ?p ?t1 ?t2)
                    )
                    (type_of_content ?ct ?t1)
                )
                    
 :effect (and (not (needs_content ?p ?t1))
              (not (needs_or_content ?p ?t1 ?t2))
              (not (contains_content ?b ?ct))
              (empty ?b)
        )
    )
    
)    
    