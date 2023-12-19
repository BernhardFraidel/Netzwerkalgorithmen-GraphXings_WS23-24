package GraphXings;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Game.GameInstance.PlanarGameInstanceFactory;
import GraphXings.Game.League.NewLeague;
import GraphXings.Game.League.NewLeagueResult;
import GraphXings.Gruppe10.ProjectionPlayer;
import GraphXings.oldGr10.OldProjectionPlayer;

import java.util.ArrayList;

public class GraphXings
{
    public static void main (String[] args)
    {
        ArrayList<NewPlayer> players = new ArrayList<>();
        //TODO: add players here
        players.add(new ProjectionPlayer());
        players.add(new OldProjectionPlayer());
        PlanarGameInstanceFactory factory = new PlanarGameInstanceFactory(887);
        long timeLimit = 300000000000l;
        NewLeague l = new NewLeague(players,3,timeLimit,factory);
        NewLeagueResult lr = l.runLeague();
        System.out.println(lr.announceResults());
    }
}
