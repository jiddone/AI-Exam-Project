(define (problem instance1)
  (:domain ia_project)

  (:objects
   r1 - robot
   c1 - carrier
   food medicine tools - type
   ct1 ct2 ct3 ct4 - content
   l1 l2 l3 - location
   b1 b2 b3 b4 b5 - box
   p1 p2 p3 - person
   s1 s2 s3 s4 - space
   
  ; max_capacity
   )
   

  (:init
  
   ;(= (max_capacity) 4)
   (carr_space c1 s1)
   (carr_space c1 s2)
   (carr_space c1 s3)
   (carr_space c1 s4)
 
   (pos_robot r1 l1)
   (pos_carr c1 l1)
   
   (pos_box b1 l1)
   (pos_box b2 l1)
   (pos_box b3 l1)
   (pos_box b4 l1)
   (pos_box b5 l1)
   
   (empty b1)
   (empty b2)
   (empty b3)
   (empty b4)
   (empty b5)

   
   (pos_content ct1 l1)
   (pos_content ct2 l1)
   (pos_content ct3 l1)
   (pos_content ct4 l1)

   (type_of_content ct1 medicine)
   (type_of_content ct2 food)
   (type_of_content ct3 medicine)
   (type_of_content ct4 food)

   
   
   (pos_person p1 l2)
   (pos_person p2 l2)
   (pos_person p3 l3)
   

   (needs_content p1 medicine)
   (needs_content p1 food)
   (needs_content p2 medicine)
   (needs_content p3 food)
   
   
   )

;; the task is to move all containers to locations l2
;; ca and cc in pile p2, the rest in q2
  (:goal
    (and (not (needs_content p1 medicine))
          (not  (needs_content p1 food))
          (not  (needs_content p2 medicine))
          (not  (needs_content p3 food))
          )
    )
)     