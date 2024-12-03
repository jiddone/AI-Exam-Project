(define (domain prova1)
 (:requirements :strips :typing :adl :durative-actions :fluents)
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

  (:functions
        (type_weight ?t - type)
        (carrier_weight ?c - carrier )

    )
  
  


(:predicates

    
    
    ;posizioni
    
    (carr_space ?c - carrier ?s - space)
    
    (pos_robot ?r - robot ?l - location) ;il robot r si trova in pos`izione l
    (pos_person ?p - person ?l - location) ; la persona p si trova alla posizione l
    (pos_box ?b - box ?l - location) ; la box b si trova alla posizione l
    
    (pos_content ?ct - content ?l - location) ; la medicina m si trova alla posizione l
    (pos_carr ?c - carrier ?l - location) ;il carrier c si trova in posizione l
    
    (not_busy_robot ?r - robot)

    (type_of_content ?ct - content ?t - type)

    (needs_content ?p - person ?t - type)

    
    ;box
    (empty ?b - box) ;indica se la scatola e' vuota
    

    (contains_content ?b - box ?ct - content )
    
    ;carrier
    (loaded_on ?b - box ?c - carrier)           ; La scatola b e' caricata sul carrier c    
    
)


;; moves a robot
 (:durative-action move_robot
     :parameters (?r - robot ?from ?to - location)  ;
     :duration (= ?duration 15)
     :condition (and (at start (pos_robot ?r ?from))
                    (at start (not_busy_robot ?r)))
     

     :effect (and (at start (pos_robot ?r ?to))
                (at end (not (pos_robot ?r ?from))) 
                (at start (not(not_busy_robot ?r)) )
                  (at end (not_busy_robot ?r))))
                  
                  
;; moves a carrier
 (:durative-action move_carrier
     :parameters (?c - carrier ?from ?to - location ?r - robot)  ;
     
     
     
     :duration (= ?duration (+ 20 (carrier_weight ?c)) )


     :condition (and (at start (pos_carr ?c ?from))
                     (at start   (pos_robot ?r ?from))
                     (at start (not_busy_robot ?r))
                )
     
     :effect (and (at end (pos_carr ?c ?to))  (at start (not (pos_carr ?c ?from))) 
                  (at end (pos_robot ?r ?to)) (at start (not (pos_robot ?r ?from)))
                  (at start (not(not_busy_robot ?r)) )
                  (at end (not_busy_robot ?r))
                  ))
                  
                  
                  
                  
; carica il carrier con una box
 (:durative-action load_carrier_with_box
     :parameters (?c - carrier ?l - location ?b - box ?r - robot ?s - space)
     :duration (= ?duration 10)
     :condition (and (over all(pos_carr ?c ?l)) 
                        (at start (pos_box ?b ?l))
                        (over all (pos_robot ?r ?l))
                        (at start(carr_space ?c ?s))
                        (at start (not_busy_robot ?r))   
                    )
                        
     :effect (and (at end (loaded_on ?b ?c))
                  (at start (not (pos_box ?b ?l)))
                  (at start (not (carr_space ?c ?s)))
                  (at start (not(not_busy_robot ?r)) )
                  (at end (not_busy_robot ?r))
            )
)




; carica il carrier con una box
 (:durative-action fill_empty_box 
     :parameters (?c - carrier ?l - location ?ct - content  ?r - robot  ?b - box ?t - type)
     :duration (= ?duration 5)
     :condition (and (over all (pos_carr ?c ?l))
                        (at start (pos_content ?ct ?l))
                        (over all (pos_robot ?r ?l))
                        (over all (loaded_on ?b ?c))
                        (at start (empty ?b))
                        (over all (type_of_content ?ct ?t))
                        (at start (not_busy_robot ?r))
                    )
                    
                        
     :effect (and (at start (contains_content ?b ?ct))
                  (at start (not (pos_content ?ct ?l)))
                  (at start (not (empty ?b)))
                  (at end (increase (carrier_weight ?c) (type_weight ?t)))
                  (at start (not(not_busy_robot ?r)) )
                  (at end (not_busy_robot ?r))
            )
)
            

(:durative-action unload_content
 :parameters (?c - carrier ?l - location ?b - box ?r - robot ?p - person ?ct - content ?t - type)
 :duration (= ?duration 5)
 :condition (and (over all (pos_carr ?c ?l))
                    (over all (pos_robot ?r ?l))
                    (over all (pos_person ?p ?l))
                    (over all (loaded_on ?b ?c))
                    (at start (not (empty ?b)))
                    (at start (contains_content ?b ?ct))
                    (at start (needs_content ?p ?t))
                    (over all (type_of_content ?ct ?t))
                    (at start (not_busy_robot ?r))
                    
                )
                    
 :effect (and (at start (not (needs_content ?p ?t)))
              (at start (not (contains_content ?b ?ct)))
              (at end (empty ?b))
              (at end (decrease (carrier_weight ?c) (type_weight ?t)))
              (at start (not(not_busy_robot ?r)) )
              (at end (not_busy_robot ?r))
        )
    )
    
)    
    