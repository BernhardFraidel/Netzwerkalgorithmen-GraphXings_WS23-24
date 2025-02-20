package GraphXings;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Algorithms.NewRandomPlayer;
import GraphXings.Game.GameInstance.*;
import GraphXings.Game.League.NewLeague;
import GraphXings.Game.League.NewLeagueResult;
import GraphXings.Game.Match.NewMatch;
import GraphXings.Game.Match.NewMatchResult;
import java.util.ArrayList;

import GUI.MainWindow;

public class GraphXings
{
 
 // original main()-function
     
/*     public static void main (String[] args)
    {
        ArrayList<NewPlayer> players = new ArrayList<>();
        players.add(new NewRandomPlayer("R1"));
        players.add(new NewRandomPlayer("R2"));
        players.add(new NewRandomPlayer("R3"));
        long timeLimit = 300000000000l;
        long seed = 27081883;
        int bestOf = 1;
        NewMatch.MatchType matchType = NewMatch.MatchType.CROSSING_ANGLE;
        PlanarGameInstanceFactory factory = new PlanarGameInstanceFactory(seed);
        runLeague(players,bestOf,timeLimit,factory,matchType,seed);
        //runRemainingMatches(player,players,bestOf,timeLimit,factory);
    }
*/

    /**
     * main()-function for the GUI
     * @param args
     */
    public static void main (String[] args)
    {
        System.setProperty("sun.java2d.uiScale", "2.0");


        MainWindow.getInstance();
    }

    // The following functions are part of the MainWindow class now.
    /*
    public static void runLeague(ArrayList<NewPlayer> players, int bestOf, long timeLimit, GameInstanceFactory factory, NewMatch.MatchType matchType, long seed)
    {
        NewLeague l = new NewLeague(players,bestOf,timeLimit,factory,matchType,seed);
        NewLeagueResult lr = l.runLeague();
        System.out.println(lr.announceResults());
    }
    public static void runRemainingMatches(NewPlayer p1, ArrayList<NewPlayer> opponents, int bestOf, long timeLimit, GameInstanceFactory factory, NewMatch.MatchType matchType, long seed)
    {
        int i = 1;
        for (NewPlayer opponent : opponents)
        {
            NewMatch m = new NewMatch(p1,opponent,factory,bestOf,timeLimit,matchType,seed);
            NewMatchResult mr = m.play();
            System.out.println("Match " + i++ + ": " + mr.announceResult());
        }
    }
    */
}
