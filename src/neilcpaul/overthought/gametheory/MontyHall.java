package neilcpaul.overthought.gametheory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: neilcpaul
 * Date: 27/11/14
 * Time: 21:17
 * Model the Monty Hall Problem, and report the results through this object.
 */
public class MontyHall {

    //Capture data
    int sw_wins = 0; // Switching wins
    int sw_losses = 0; // Switching losses
    int ns_wins = 0; // Not switching wins
    int ns_losses = 0; //Not switching losses

    // Parameters
    // Set the number of iterations. Even number pls.
    int totalIterationsEach;
    int paths;

    // Things to play about with
    private static int pathsDefault = 3;
    private static int iterationsEachDefault = 500000;

    // Random generator
    Random randomGen = new Random();

    public  MontyHall()
    {
        this(iterationsEachDefault);
    }

    public MontyHall(int iterations)
    {
        this(iterations, pathsDefault);
    }

    public MontyHall(int iterations, int outcomes)
    {
        totalIterationsEach = iterations;
        paths = outcomes;

        System.out.println("Study of the probability of outcomes in the following scenario:\n"
                + "There is a choice of 3 outcomes, which are hidden.\n"
                + "1 win scenario\n"
                + "2 lose scenarios.\n\n"
                + "One outcome is chosen.\n"
                + "Of the two left, a lose scenario is revealed.\n"
                + "The choice is given to switch to the remaining hidden outcome\n"
                + "After choosing, the outcomes are revealed."
                + "- Is it better to stay or switch?\n\n"
                + "Running simulation...\n"
        );
    }

    public void runModel()
    {
        runSim();
        printResults();
    }

    private void printResults() {
        System.out.println("Completed simulation. Results:\n");
        System.out.println("\nOVERALL");
        System.out.println("Number of runs         :" + totalIterationsEach*2);
        System.out.println("Number of wins         :" + sw_wins+ns_wins);
        System.out.println("Number of losses       :" + sw_losses+ns_losses);
        System.out.println("Percentage wins        :" + ((sw_wins+ns_wins)*100)/(totalIterationsEach*2));
        System.out.println("Percentage losses      :" + ((sw_losses+ns_losses)*100)/(totalIterationsEach*2));

        System.out.println("\nNOT SWITCHING:");
        System.out.println("Number of runs     :" + totalIterationsEach);
        System.out.println("Number of wins     :" + ns_wins);
        System.out.println("Number of losses   :" + ns_losses);
        System.out.println("Percentage wins    :" + (ns_wins*100)/totalIterationsEach);
        System.out.println("Percentage losses  :" + (ns_losses*100)/totalIterationsEach);

        System.out.println("\nSWITCHING");
        System.out.println("Number of runs         :" + totalIterationsEach);
        System.out.println("Number of wins         :" + sw_wins);
        System.out.println("Number of losses       :" + sw_losses);
        System.out.println("Percentage wins        :" + (sw_wins*100)/totalIterationsEach);
        System.out.println("Percentage losses      :" + (sw_losses*100)/totalIterationsEach);


        System.out.println("\nSUMMARY:");
        System.out.println("Percentage more wins by " + ((ns_wins>sw_wins)?"not switching":"switching") + ": "
                        + ((ns_wins>sw_wins?ns_wins:sw_wins)*100)/totalIterationsEach);

    }

    private void runSim()
    {
        // Work out the total number of runs
        int totalIterations = totalIterationsEach*2;

        // Start playing each round, taking note of the choice and outcome
        for (int i = 0; i < totalIterations; i++)
        {
            // For the first half of rounds: switch, and for the second half: stay.
            boolean doSwitch = i<totalIterationsEach;
            Outcome outcome = playRound(doSwitch);

            // Take note of the result
            if (outcome==Outcome.WIN & doSwitch)
            {
                sw_wins++;
            } else if (outcome==Outcome.LOSE && doSwitch)
            {
                sw_losses++;
            } else if (outcome==Outcome.WIN && !doSwitch)
            {
                ns_wins++;
            } else
            {
                ns_losses++;
            }
        }
    }

    public Outcome playRound(boolean doSwitch)
    {
        // Set up the game
        int initialChoice;
        int currentChoice;
        int winOutcome;

        // Explicitly creating the outcome list, and the reveal set
        final List<Outcome> outcomes = new ArrayList<>();
        final Set<Integer> hidden = new TreeSet<>();

        // Pick win outcome. paths = number of outcomes
        winOutcome = randomGen.nextInt(paths);

        // Populate the path choices
        for (int i = 0; i<paths; i++)
        {
            if (i==winOutcome)
            {
                outcomes.add(Outcome.WIN);
            } else
            {
                outcomes.add(Outcome.LOSE);
            }
            hidden.add(i);
        }

        // Make initial choice. This could be a win outcome or one of two lose outcomes.
        initialChoice = randomGen.nextInt(paths);
        currentChoice = initialChoice;

        // Revealing a lose outcome
        // Use 'hidden' to keep track of what is left
        // We can't reveal the initial choice, or win outcome
        // Since these could be the same thing, we will remove a random LOSE path that isnt the current choice
        // In implementation:
        // Remove winOutcome and currentChoice from set
        // Pull a remaining LOSE randomly
        // Add winOutcome and currentChoice back to set for switching
        if (winOutcome!=currentChoice)
        {
            hidden.remove(winOutcome);
        }
        hidden.remove(currentChoice);

        // Quick sanity check that there are actually values to remove
        if (hidden.size()>0)
        {
            // Pick a hidden lose value to remove at random based on hidden set size
            int loseChoice = randomGen.nextInt(hidden.size());
            // Get the actual value from an indexed version of the set (array)
            loseChoice = hidden.toArray(new Integer[hidden.size()])[loseChoice];
            // Remove the choice
            hidden.remove(loseChoice);
        }

        // Add the 'known' values back to the set of hidden outcomes for next choice
        if (winOutcome!=currentChoice)
        {
            hidden.add(winOutcome);
        }
        hidden.add(currentChoice);

        // currentChoice is initialChoice at this point
        // Make choice or don't make choice
        if (doSwitch)
        {
            // Hidden set should have 2 values in it now
            // Remove the initial choice, as it is intended to switch
            hidden.remove(currentChoice);
            // Grab the last value left, at index 0, put into currentChoice
            currentChoice = hidden.toArray(new Integer[hidden.size()])[0];
        }

        // Check currentChoice for win (from outcomes map)
        // Return the outcome
        return outcomes.get(currentChoice);
    }

    private enum Outcome
    {
        WIN,
        LOSE
    }

}

