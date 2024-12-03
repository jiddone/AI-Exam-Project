import fr.uga.pddl4j.heuristics.state.StateHeuristic;
import fr.uga.pddl4j.planners.InvalidConfigurationException;
import fr.uga.pddl4j.planners.LogLevel;
import fr.uga.pddl4j.planners.statespace.HSP;

public class Planner {


    public static void main(String[] args) {

        // The path directory
        final String path = "planner/";

        // Creates the planner
        HSP planner = new HSP();
        // Sets the domain of the problem to solve
        planner.setDomain(path + "domain.pddl");
        // Sets the problem to solve
        planner.setProblem(path + "problem1.pddl");
        // Sets the timeout of the search in seconds
        planner.setTimeout(1000);
        // Sets log level
        planner.setLogLevel(LogLevel.INFO);
        // Selects the heuristic to use
        planner.setHeuristic(StateHeuristic.Name.MAX);
        // Sets the weight of the heuristic
        planner.setHeuristicWeight(1.2);

        // Solve and print the result
        try {
            planner.solve();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }
}