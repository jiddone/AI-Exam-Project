(define (problem instance1)
  (:domain ia_project)
    

  (:objects
   r1 r2 - robot
   c1 c2 - carrier
   food medicine tools - type
   ct1 ct2 ct3 ct4 ct5 ct6 ct7 ct8 ct9 ct10 ct11 ct12 - content
   l1 l2 l3 l4 l5 l6 - location
   b1 b2 b3 - box
   p1 p2 p3 p4 p5 p6 - person
   s1 s2 s3 s4 - space
   

   )
   

  (:init
  

   (carr_space c1 s1)
   (carr_space c1 s2)
   (carr_space c2 s3)
   (carr_space c2 s4)
 
   (pos_robot r1 l1)
   (pos_robot r2 l1)
   (pos_carr c1 l1)
   (pos_carr c2 l1)
   
   (pos_box b1 l1)
   (pos_box b2 l1)
   (pos_box b3 l1)

   
   (empty b1)
   (empty b2)
   (empty b3)

  
   
   (pos_content ct1 l1)
   (pos_content ct2 l1)
   (pos_content ct3 l1)
   (pos_content ct4 l1)
   (pos_content ct5 l1)
   (pos_content ct6 l1)
   (pos_content ct7 l1)
   (pos_content ct8 l1)
   (pos_content ct9 l1)
   (pos_content ct10 l1)
   (pos_content ct11 l1)
   (pos_content ct12 l1)

   (type_of_content ct1 food)
   (type_of_content ct2 tools)
   (type_of_content ct3 medicine)
   (type_of_content ct4 medicine)
   (type_of_content ct5 medicine)
   (type_of_content ct6 food)
   (type_of_content ct7 medicine)
   (type_of_content ct8 tools)
   (type_of_content ct9 food)
   (type_of_content ct10 medicine)
   (type_of_content ct11 tools)
   (type_of_content ct12 food)

   
   (pos_person p1 l2)
   (pos_person p2 l2)
   (pos_person p3 l3)
   (pos_person p4 l4)
   (pos_person p5 l5)
   (pos_person p6 l6)
   
   
   (needs_or_content p1 food tools)
   (needs_content p2 medicine)
   (needs_content p3 medicine)
   (needs_content p4 medicine)
   (needs_content p4 food)
   (needs_content p5 medicine)
   (needs_content p5 food)
   (needs_content p5 tools)
   (needs_content p6 medicine)
   (needs_content p6 food)
   (needs_content p6 tools)

   
   
   )


  (:goal
    (and        
                (not (needs_or_content p1 food tools))
                (not (needs_content p2 medicine))
                (not (needs_content p3 medicine))
                (not (needs_content p4 medicine))
                (not (needs_content p4 food))
                (not (needs_content p5 medicine))
                (not (needs_content p5 food))
                (not (needs_content p5 tools))
                (not (needs_content p6 medicine))
                (not (needs_content p6 food))
                (not (needs_content p6 tools))
          )
    )
)     