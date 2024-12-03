import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.operator.Action;

public class MyHeuristic {
    int numContent;
    public MyHeuristic(int numContent){
        this.numContent=numContent;
    }

    public double calcCurrHeur(Node current, Problem problem) {
        Node n = current; //set node n to current
        double value=numContent; //value is the heuristic to return
        while (n.getAction() != -1) {   //iterate until the node n is the root
            final Action a = problem.getActions().get(n.getAction());  //get the action of node n
            value += getValue(a);  // increase the heuristic by the value returned from the function getValue()
            n = n.getParent();  //trace back to the parent node
        }
        return value;
    }


    public double getValue(Action currentAction) {  //take an action and return a weight associated to the action
        switch (currentAction.getName()){
            case "unload_content":  return -1;
            case "load_carrier_with_box": return -0.8;
            case "move_carrier":  return +0.5;
            case "fill_empty_box": return -0.4;
            case "move_robot":  return +1;
            case "unload_carrier_with_box": return +1;
            default: return 0;
        }
    }

    public double calcNextHeur(Node next, Problem problem, double currHeur){ // take a Node next, a Problem problem and the value of heuristic for the parent of next and compute the heuristic for node next
        double value=currHeur;
        Action currentAction = problem.getActions().get(next.getAction()); //get the action of node next
        value += getValue(currentAction); // increase the heuristic by the value returned from the function getValue()
        return value;
    }
}


/*
class MyHeuristic2 {
    int numContent;
    int numBox=4;
    private double valueLoadCarrier;

    public MyHeuristic2(int numContent){
        this.numContent=numContent;
    }

    public double[] calcCurrHeur(Node current, Problem problem) {
        double[] res=new double[3];
        Node n = current;
        double value=numContent;
        int currContentDelivered=0;
        String postAction="";
        while (n.getAction() != -1) {

            final Action a = problem.getActions().get(n.getAction());
            //System.out.println(a.getName());


            double val = getValue(a);
            value += val;
            if (val == -1)
                currContentDelivered++;

            postAction=a.getName();
            n = n.getParent();
        }

        res[0]=value;
        res[1]=currContentDelivered;

        return res;
    }


    public double getValue(Action currentAction) {
        if(currentAction.getName().equals("unload_content")){
            return -1;
        }
        else if(currentAction.getName().equals("load_carrier")){
            return valueLoadCarrier;
        }
        else if(currentAction.getName().equals("unload_carrier")){
            return +0.5;
        }
        else if(currentAction.getName().equals("move_carrier")){
            return +0.4;
        }
        else if(currentAction.getName().equals("fill_box")){
            return -0.8;
        }
        else if(currentAction.getName().equals("move_robot")){
            return +1;
        }
        return 0;
    }

    public double calcNextHeur(Node next, Problem problem, double[] info){
        double value=info[0];
        Action currentAction = problem.getActions().get(next.getAction());
        //String parentAction="";
        //if(next.getParent().getAction() != -1)
        //     parentAction = problem.getActions().get(next.getParent().getAction()).getName();

       // if(numContent-info[1]<=numBox && (parentAction.equals("unload_content"))){
            //System.out.println("ddw");
        //    valueLoadCarrier=+2;
        //}
        //else{
            valueLoadCarrier=-0.4;
        //}
        value += getValue(currentAction);
        //System.out.println(value);
        return value;
    }
}

 */