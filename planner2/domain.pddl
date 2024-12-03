(define (domain prova1)
 (:requirements :strips :typing :negative-preconditions :disjunctive-preconditions :conditional-effects)
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

    ;(current_capacity ?c - carrier ?n - integer)
    
    ;posizioni
    
    (carr_space ?c - carrier ?s - space)
    
    (pos_robot ?r - robot ?l - location) ;il robot r si trova in pos`izione l
    (pos_person ?p - person ?l - location) ; la persona p si trova alla posizione l
    (pos_box ?b - box ?l - location) ; la box b si trova alla posizione l
    
    (pos_content ?ct - content ?l - location) ; la medicina m si trova alla posizione l
    (pos_carr ?c - carrier ?l - location) ;il carrier c si trova in posizione l
    
    ;persone
    ;(needs_food ?p - person) ; la persona p ha bisogno di cibo 
    ;(needs_tool ?p - person) ; la persona p ha bisogno di tool 
    ;(needs_medicine ?p - person) ; la persona p ha bisogno di medicine

    (type_of_content ?ct - content ?t - type)

    (needs_content ?p - person ?t - type)
    (needs_or_content ?p -person ?t1 - type ?t2 - type)

    ;box
    (empty ?b - box) ;indica se la scatola e' vuota
    (contains_content ?b - box ?ct - content )
    
    ;carrier
    (loaded_on ?b - box ?c - carrier)           ; La scatola b e' caricata sul carrier c
    
    
    
    
)


;; moves a robot
 (:action move_robot
     :parameters (?r - robot ?from ?to - location)  ;
     :precondition (and (pos_robot ?r ?from)
                    )
     
     :effect (and (pos_robot ?r ?to)
                (not (pos_robot ?r ?from)) ))
                  
                  
;; moves a carrier
 (:action move_carrier
     :parameters (?c - carrier ?from ?to - location ?r - robot)  ;
     :precondition (and (pos_carr ?c ?from)
                        (pos_robot ?r ?from)
                        )
     
     :effect (and (pos_carr ?c ?to) (not (pos_carr ?c ?from)) 
                  (pos_robot ?r ?to) (not (pos_robot ?r ?from)) 
                  ))
                  
                  
                  
                  
; carica il carrier con una box
 (:action load_carrier
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

;scarica una box dal carrier
(:action unload_carrier
 :parameters (?c - carrier ?s - space ?l - location ?b - box ?r - robot)
 :precondition (and (pos_carr ?c ?l) 
                    (pos_robot ?r ?l)
                    (loaded_on ?b ?c)
                    (not(carr_space ?c ?s))
                )
                    
 :effect (and (not(loaded_on ?b ?c))
              (pos_box ?b ?l)
              (carr_space ?c ?s)
        )
    )




; riempie una box
 (:action fill_box
     :parameters (?l - location ?ct - content  ?r - robot  ?b - box)
     :precondition (and (pos_content ?ct ?l)
                        (pos_box ?b ?l)
                        (pos_robot ?r ?l)
                        (empty ?b)
                    )
                        
     :effect (and (contains_content ?b ?ct)
                  (not (pos_content ?ct ?l))
                  (not (empty ?b))
            )
)
            
;scarica il content da una box
(:action unload_content
 :parameters (?l - location ?b - box ?r - robot ?p - person ?ct - content ?t1 - type ?t2 - type)
 :precondition (and 
                    (pos_robot ?r ?l)
                    (pos_person ?p ?l)
                    (pos_box ?b ?l)
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
    