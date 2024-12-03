/*
 * Copyright (c) 2021 by Damien Pellier <Damien.Pellier@imag.fr>.
 *
 * This file is part of PDDL4J library.
 *
 * PDDL4J is free software: you can redistribute it and/or modify * it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * PDDL4J is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License * along with PDDL4J.  If not,
 * see <http://www.gnu.org/licenses/>
 */

import fr.uga.pddl4j.heuristics.state.StateHeuristic;
 import fr.uga.pddl4j.parser.DefaultParsedProblem;
 import fr.uga.pddl4j.parser.RequireKey;
 import fr.uga.pddl4j.plan.Plan;
 import fr.uga.pddl4j.plan.SequentialPlan;
 import fr.uga.pddl4j.planners.LogLevel;
 import fr.uga.pddl4j.planners.AbstractPlanner;
import fr.uga.pddl4j.planners.InvalidConfigurationException;
import fr.uga.pddl4j.planners.Planner;
 import fr.uga.pddl4j.planners.PlannerConfiguration;
 import fr.uga.pddl4j.planners.ProblemNotSupportedException;
import fr.uga.pddl4j.problem.DefaultProblem;
 import fr.uga.pddl4j.problem.Problem;
 import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.operator.Action;
 import fr.uga.pddl4j.problem.operator.ConditionalEffect;
import org.apache.logging.log4j.LogManager;
 import org.apache.logging.log4j.Logger;
 import picocli.CommandLine;

import java.util.*;

/**
  * The class is an example. It shows how to create a simple A* search planner able to
  * solve an ADL problem by choosing the heuristic to used and its weight.
  *
  * @author D. Pellier
  * @version 4.0 - 30.11.2021
  */
 @CommandLine.Command(name = "MyASP",
     version = "MyASP 1.0",
     description = "Solves a specified planning problem using A* search strategy.",
     sortOptions = false,
     mixinStandardHelpOptions = true,
     headerHeading = "Usage:%n",
     synopsisHeading = "%n",
     descriptionHeading = "%nDescription:%n%n",
     parameterListHeading = "%nParameters:%n",
     optionListHeading = "%nOptions:%n")
 public class MyASP extends AbstractPlanner {
     private boolean debug=false;
 
     /**
      * The class logger.
      */
     private static final Logger LOGGER = LogManager.getLogger(MyASP.class.getName());
 
     /**
      * The HEURISTIC property used for planner configuration.
      */
     public static final String HEURISTIC_SETTING = "HEURISTIC";
 
     /**
      * The default value of the HEURISTIC property used for planner configuration.
      */
     public static final StateHeuristic.Name DEFAULT_HEURISTIC = StateHeuristic.Name.MAX;
 
     /**
      * The WEIGHT_HEURISTIC property used for planner configuration.
      */
     public static final String WEIGHT_HEURISTIC_SETTING = "WEIGHT_HEURISTIC";
 
     /**
      * The default value of the WEIGHT_HEURISTIC property used for planner configuration.
      */
     public static final double DEFAULT_WEIGHT_HEURISTIC = 1.0;
 
     /**
      * The weight of the heuristic.
      */
     private double heuristicWeight;
 
     /**
      * The name of the heuristic used by the planner.
      */
     private StateHeuristic.Name heuristic;

     private int numberOfContents; // numbers of contents to deliver

     private int stateVisited=0; //number of visited state
 
     /**
      * Creates a new A* search planner with the default configuration.
      */
     public MyASP() {
         this(MyASP.getDefaultConfiguration());
     }
 
     /**
      * Creates a new A* search planner with a specified configuration.
      *
      * @param configuration the configuration of the planner.
      */
     public MyASP(final PlannerConfiguration configuration) {
         super();
         this.setConfiguration(configuration);
     }
 
     /**
      * Sets the weight of the heuristic.
      *
      * @param weight the weight of the heuristic. The weight must be greater than 0.
      * @throws IllegalArgumentException if the weight is strictly less than 0.
      */
     @CommandLine.Option(names = {"-w", "--weight"}, defaultValue = "1.0",
         paramLabel = "<weight>", description = "Set the weight of the heuristic (preset 1.0).")
     public void setHeuristicWeight(final double weight) {
         if (weight <= 0) {
             throw new IllegalArgumentException("Weight <= 0");
         }
         this.heuristicWeight = weight;
     }
 
     /**
      * Set the name of heuristic used by the planner to the solve a planning problem.
      *
      * @param heuristic the name of the heuristic.
      */
     @CommandLine.Option(names = {"-e", "--heuristic"}, defaultValue = "FAST_FORWARD",
         description = "Set the heuristic : AJUSTED_SUM, AJUSTED_SUM2, AJUSTED_SUM2M, COMBO, "
             + "MAX, FAST_FORWARD SET_LEVEL, SUM, SUM_MUTEX (preset: FAST_FORWARD)")
     public void setHeuristic(StateHeuristic.Name heuristic) {
         this.heuristic = heuristic;
     }
 
     /**
      * Returns the name of the heuristic used by the planner to solve a planning problem.
      *
      * @return the name of the heuristic used by the planner to solve a planning problem.
      */
     public final StateHeuristic.Name getHeuristic() {
         return this.heuristic;
     }
 
     /**
      * Returns the weight of the heuristic.
      *
      * @return the weight of the heuristic.
      */
     public final double getHeuristicWeight() {
         return this.heuristicWeight;
     }
 
     /**
      * Instantiates the planning problem from a parsed problem.
      *
      * @param problem the problem to instantiate.
      * @return the instantiated planning problem or null if the problem cannot be instantiated.
      */
     @Override
     public Problem instantiate(DefaultParsedProblem problem) {
         final Problem pb = new DefaultProblem(problem);
         pb.instantiate();
         return pb;
     }
 
     /**
     * Search a solution plan to a specified domain and problem using A*.
     *
     * @param problem the problem to solve.
     * @return the plan found or null if no plan was found.
     */
    @Override
    public Plan solve(final Problem problem) {
        LOGGER.info("* Starting My A* search \n");
        Plan plan = null;
        // Search a solution
        try {
            final long begin = System.currentTimeMillis();
            plan = this.astar(problem);
            final long end = System.currentTimeMillis();
            // If a plan is found update the statistics of the planner
        // and log search information
            if (plan != null) {
                LOGGER.info("* My A* search succeeded\n");
                this.getStatistics().setTimeToSearch(end - begin);

            } else {
                LOGGER.info("* My A* search failed\n");
            }
               
        } catch (ProblemNotSupportedException e) {
            LOGGER.error("not supported problem");
            e.printStackTrace();
        }
        finally {
            // Return the plan found or null if the search fails.
            return plan;
        }
    }
    
     /**
      * Checks the planner configuration and returns if the configuration is valid.
      * A configuration is valid if (1) the domain and the problem files exist and
      * can be read, (2) the timeout is greater than 0, (3) the weight of the
      * heuristic is greater than 0 and (4) the heuristic is a not null.
      *
      * @return <code>true</code> if the configuration is valid <code>false</code> otherwise.
      */
     public boolean hasValidConfiguration() {
         return super.hasValidConfiguration()
             && this.getHeuristicWeight() > 0.0
             && this.getHeuristic() != null;
     }
 
     /**
      * This method return the default arguments of the planner.
      *
      * @return the default arguments of the planner.
      * @see PlannerConfiguration
      */
     public static PlannerConfiguration getDefaultConfiguration() {
         PlannerConfiguration config = Planner.getDefaultConfiguration();
         config.setProperty(MyASP.HEURISTIC_SETTING, MyASP.DEFAULT_HEURISTIC.toString());
         config.setProperty(MyASP.WEIGHT_HEURISTIC_SETTING,
             Double.toString(MyASP.DEFAULT_WEIGHT_HEURISTIC));
         return config;
     }
 
     /**
      * Returns the configuration of the planner.
      *
      * @return the configuration of the planner.
      */
     @Override
     public PlannerConfiguration getConfiguration() {
         final PlannerConfiguration config = super.getConfiguration();
         config.setProperty(MyASP.HEURISTIC_SETTING, this.getHeuristic().toString());
         config.setProperty(MyASP.WEIGHT_HEURISTIC_SETTING, Double.toString(this.getHeuristicWeight()));
         return config;
     }
 
     /**
      * Sets the configuration of the planner. If a planner setting is not defined in
      * the specified configuration, the setting is initialized with its default value.
      *
      * @param configuration the configuration to set.
      */
     @Override
     public void setConfiguration(final PlannerConfiguration configuration) {
         super.setConfiguration(configuration);
         if (configuration.getProperty(MyASP.WEIGHT_HEURISTIC_SETTING) == null) {
             this.setHeuristicWeight(MyASP.DEFAULT_WEIGHT_HEURISTIC);
         } else {
             this.setHeuristicWeight(Double.parseDouble(configuration.getProperty(
                 MyASP.WEIGHT_HEURISTIC_SETTING)));
         }
         if (configuration.getProperty(MyASP.HEURISTIC_SETTING) == null) {
             this.setHeuristic(MyASP.DEFAULT_HEURISTIC);
         } else {
             this.setHeuristic(StateHeuristic.Name.valueOf(configuration.getProperty(
                 MyASP.HEURISTIC_SETTING)));
         }
     }
 
     /**
      * Search a solution plan for a planning problem using an A* search strategy.
      *
      * @param problem the problem to solve.
      * @return a plan solution for the problem or null if there is no solution
      * @throws ProblemNotSupportedException if the problem to solve is not supported by the planner.
      */
     public Plan astar(Problem problem) throws ProblemNotSupportedException {
         // Check if the problem is supported by the planner
         if (!this.isSupported(problem)) {
             throw new ProblemNotSupportedException("Problem not supported");
         }
 
         // First we create an instance of the heuristic to use to guide the search
         final Heuristic heuristic = new Heuristic(problem);
 
         // We get the initial state from the planning problem
         final State init = new State(problem.getInitialState());
 
         // We initialize the closed list of nodes (store the nodes explored)
         final Set<Node> close = new HashSet<>();
 
         // We initialize the opened list to store the pending node according to function f
         final double weight = this.getHeuristicWeight();
         final PriorityQueue<Node> open = new PriorityQueue<>(1000, new Comparator<Node>() {
             public int compare(Node n1, Node n2) {
                 double f1 = weight * n1.getHeuristic() + n1.getCost();
                 double f2 = weight * n2.getHeuristic() + n2.getCost();
                 return Double.compare(f1, f2);
             }
         });


         numberOfContents = problem.getDomains().get(7).size();       // read the number of contents from the problem file
         System.out.println("Number of contents to deliver "+ numberOfContents);  // print the number of contents
         MyHeuristic h=new MyHeuristic(2*numberOfContents);   //create the object for compute heuristic


         // We create the root node of the tree search
         final Node root = new Node(init, null, -1, 0, 2*numberOfContents);
 
         // We add the root to the list of pending nodes
         open.add(root);
         Plan plan = null;

         // We set the timeout in ms allocated to the search
         final int timeout = this.getTimeout() * 1000;
         long time = 0;




         // We start the search
         while (!open.isEmpty() && plan == null && time < timeout) {
 
             // We pop the first node in the pending list open
             final Node current = open.poll();
             close.add(current);
 
             // If the goal is satisfied in the current node then extract the search and return it
             if (current.satisfy(problem.getGoal())) {
                 return this.extractPlan(current, problem);
             } else { // Else we try to apply the actions of the problem to the current node

                 double currentHeuristic=h.calcCurrHeur(current, problem);  //compute the heuristic on current node

                 for (int i = 0; i < problem.getActions().size(); i++) {
                     // We get the actions of the problem
                     Action a = problem.getActions().get(i);
                     stateVisited++;
                     // If the action is applicable in the current node
                     if (a.isApplicable(current)) {
                         //System.out.println(p);

                         Node next = new Node(current);
                         // We apply the effect of the action
                         final List<ConditionalEffect> effects = a.getConditionalEffects();
                         for (ConditionalEffect ce : effects) {
                             if (current.satisfy(ce.getCondition())) {
                                 next.apply(ce.getEffect());
                             }
                         }

                         // We set the new child node information
                         final double g = current.getCost() + 1;
                         if (!close.contains(next)) {
                             next.setCost(g);
                             next.setParent(current);
                             next.setAction(i);
                             next.setHeuristic(h.calcNextHeur(next,problem,currentHeuristic)); //compute the heuristic on next node
                             open.add(next);
                         }
                     }
                 }
             }
         }

         // Finally, we return the search computed or null if no search was found
         return plan;
     }





    /**
      * Extracts a search from a specified node.
      *
      * @param node    the node.
      * @param problem the problem.
      * @return the search extracted from the specified node.
      */
     private Plan extractPlan(final Node node, final Problem problem) {
         Node n = node;
         final Plan plan = new SequentialPlan();
         while (n.getAction() != -1) {
             final Action a = problem.getActions().get(n.getAction());
             plan.add(0, a);
             n = n.getParent();
         }
         return plan;
     }
 
     /**
      * Returns if a specified problem is supported by the planner. Just ADL problem can be solved by this planner.
      *
      * @param problem the problem to test.
      * @return <code>true</code> if the problem is supported <code>false</code> otherwise.
      */
     @Override
     public boolean isSupported(Problem problem) {
         return !problem.getRequirements().contains(RequireKey.ACTION_COSTS)
                 && !problem.getRequirements().contains(RequireKey.CONSTRAINTS)
                 && !problem.getRequirements().contains(RequireKey.CONTINOUS_EFFECTS)
                 && !problem.getRequirements().contains(RequireKey.DERIVED_PREDICATES)
                 && !problem.getRequirements().contains(RequireKey.DURATIVE_ACTIONS)
                 && !problem.getRequirements().contains(RequireKey.DURATION_INEQUALITIES)
                 && !problem.getRequirements().contains(RequireKey.FLUENTS)
                 && !problem.getRequirements().contains(RequireKey.GOAL_UTILITIES)
                 && !problem.getRequirements().contains(RequireKey.METHOD_CONSTRAINTS)
                 && !problem.getRequirements().contains(RequireKey.NUMERIC_FLUENTS)
                 && !problem.getRequirements().contains(RequireKey.OBJECT_FLUENTS)
                 && !problem.getRequirements().contains(RequireKey.PREFERENCES)
                 && !problem.getRequirements().contains(RequireKey.TIMED_INITIAL_LITERALS)
                 && !problem.getRequirements().contains(RequireKey.HIERARCHY);
     }
     
     /**
      * The main method of the <code>MyASP</code> planner.
      *
      * @param args the arguments of the command line.
      */
      public static void main(String[] args) {

        // The path to the benchmarks directory
        final String benchmarks = "planner/";

        // Gets the default configuration from the planner
        PlannerConfiguration config = MyASP.getDefaultConfiguration();
        // Sets the domain of the problem to solve
        config.setProperty(MyASP.DOMAIN_SETTING, benchmarks + "domain.pddl");
        // Sets the problem to solve
        config.setProperty(MyASP.PROBLEM_SETTING, benchmarks + "problem3.pddl");
        // Sets the timeout allocated to the search.
        config.setProperty(MyASP.TIME_OUT_SETTING, 600);
        // Sets the log level
        config.setProperty(MyASP.LOG_LEVEL_SETTING, LogLevel.INFO);
        // Sets the weight of the heuristic
        config.setProperty(MyASP.WEIGHT_HEURISTIC_SETTING, 5.7);  //  1 - 3.5  13      2 - 3.5 35     3 - 5.7 - 59

        // Creates an instance of the MyASP planner with the specified configuration
        final MyASP planner = new MyASP(config);
        // Runs the planner and print the solution
        try {
            planner.solve();
            LOGGER.info("State visited: "+planner.stateVisited);  //print the number of visited state
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
 }