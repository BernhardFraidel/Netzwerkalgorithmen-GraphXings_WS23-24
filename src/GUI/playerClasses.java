package GUI;

import java.util.Collection;
import java.util.HashMap;

import java.util.EnumSet;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Algorithms.NewRandomPlayer;
import GraphXings.Gruppe10.ProjectionPlayer;
import GraphXings.Gruppe10.AngleProjectionPlayer;

public class playerClasses 
{
    

    public  playerClasses()
    {
        forwardMap = new HashMap<Integer,String>();
        reverseMap = new HashMap<String, Integer>();

        // TODO FOR NEW PLAYER CLASS
        // add new players manually
        addPlayer(0, "Random Player");
        addPlayer(1, "Projection Player");
        addPlayer(2, "Angle Projection Player");
    }


    private void addPlayer(int key, String value)
    {
        forwardMap.put(key, value);
        reverseMap.put(value, key);
    }


    public int size()
    {
        return forwardMap.size();
    }

    public String name(int type)
    {
        return forwardMap.get(type);
    }

    public Collection<String> names()
    {
        return forwardMap.values();
    }

    public Collection<Integer> keys()
    {
        return reverseMap.values();
    }

    public int type(String name)
    {
        return reverseMap.get(name);
    }



    public NewPlayer createPlayer(int type, String name)
    {
        // TODO FOR NEW PLAYER CLASS
        // add new players manually

        switch (type) {
            case 0:
                return new NewRandomPlayer(name);

            case 1: 
                return new ProjectionPlayer();

            case 2:
                return new AngleProjectionPlayer(name);

            default:
                return null;
        }
    }

    public NewPlayer createPlayer(int type)
    {
        // TODO FOR NEW PLAYER CLASS
        // add new players manually

        switch (type) {
            case 0:
                return new NewRandomPlayer(name(type));

            case 1: 
                return new ProjectionPlayer();

            case 2:
                return new AngleProjectionPlayer(name(type));

            default:
                return null;
        }
    }

    private HashMap<Integer, String> forwardMap;
    private HashMap<String, Integer> reverseMap;
}
