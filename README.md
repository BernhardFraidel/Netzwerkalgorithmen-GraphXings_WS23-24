# GraphXings

An implementation of the asymmetric game GraphXings where two players 1 and 2 compete against each other in two game rounds. In each turn, the player is to position an unplaced vertex of a known graph on the integer grid. In the first round, the goal of player 1 is to create as many crossings as possible whereas player 2 attempts to avoid crossings. In the second round the objectives are swapped. The winner is the player who created more crossings when maximizing the number of crossings.

See https://i11www.iti.kit.edu/projects/cyclexings/index for a human-playable instance where the graph is always a cycle.

The structure of the package GraphXings is as follows:
- Package Algorithms contains algorithmic solutions.
  * The interface Player can be implemented to obtain a playing agent. Class RandomPlayer is a sample implementation performing random moves.
  * Class CrossingCalculator contains a simple function to compute the number of crossings.
- Package Data contains required data types.
  * Classes Graph, Vertex, Edge implement a graph data structure using an adjacency list representation.
  * Class Coordinate and Segment represent Points and Segments in 2D, respectively.
  * Class Rational implements calculations with rational numbers for more precise crossing computation compared to float.
- Package Game contains the game engine.
  * Class Game implements the main functionality of GraphXings.
  * Classes GameMove and GameResult are wrappers to convey results of moves and full games, respectively.
  * Class InvalidMoveException provides an exception that can be thrown in case a player attempts to cheat.
- The main function is contained in GraphXings.java. It creates a 10-cycle and instantiates a game with two random players. Then it lets them play against each other and displays the result.

If you want to implement a new player, your starting point is the interface Player in Package Algorithms.


# Player
Following is a short overview of the different implemented palyers
## BruteForcePlayer
This player tries to place every free vertex on all free coordinates 
and records in how many crossings the vertex and coordinate results.
For the minimizer one vertex with a minimal set of crossings is selected.
For the maximizer one vertex with the maximium possible crossings in the current instance of the graph is chosen.

**Pro:** 
- The minimal and maximal number of crossings for the current graph is found.

**Con:**
- Since every free vertex needs to be checked for every coordinate we have a poor runtime
  - Runtime: **O(n\*m)** where n = #vertices, m = #coordinates

## GridPlayer
- sample 5 random vertices 
  - **Optional**: include neighbourhood of placed in sampling decision
- Partition the field in a grid and sample:
  - one random coordinate that lies in the tile 
  - the one in the middle of the tile if free. if not search from middle outward.
  - **Optional**: search in far away tiles for maximiser and in close tiles for minimizer
    - only makes real sense when neighbours of placed nodes are sampled.
- Minimizer:
  - when vertex coordinate with zero crossings found => stop searching

## DistancePlayer
The Idea here is, that by placing vertices close to each other it is less likely for the random player to cross 
an edge, with maximiser it's the opposite the longer the edges the higher the chance of a crossing.
- find 1 random free neighbour of placed vertices
- get random grid coordinates
- check distance from current coordinate to grid coordinates
- minimizer: 
  - check 2 closest grid coordinates cossing numbers 
  - place vertex as close as possible with minimal crossings
- maximizer: 
  - check 2 farthest grid coordinates crossing number 
  - place Vertex as far away as possible with maximal crossings



# CrossingCalculator
## BentleyOttmannCrossingCalculator
In order to improve the runtime of native CrossingCalculator, we implemented 
a crossing calculator using the Bentley-Ottman Algorithm. 

It uses a sweep line approach to reduce the number of necessary comparisons 
between segments which improves vastly on the expected runtime of the 
given crossing calculator (O(n*n) -> O((n+k)log n).

#### Possible improvement:
Don't look at the whole graph, only consider the x interval in which the newly 
added segments lay.

### Ideen zur Verbesserung der Performance
#### Crossings schneller/besser berechnen
- J.Balaban An optimal algorithm for finding segments intersections O(N logN N + K)
#### Nicht alle möglichen positionen sampeln 
- zufällig x viele wählen und bestes nehmen
- fläche in grid aufteilen und immer nur ein sampel aus jedem grid feld
- für den minimierer: wenn crossingcalc >= der bisher gefundenen corssing => continue with next vertex/position


Idee:
Feld in Grid unterteilen:
- aus jedem grid nur eine position sampeln
  - potentiell random
  - potentiell von der mitte starten und wenn nicht frei kreisförmig drum rum suchen
- Nicht jedes grid feld sampeln
  - distanz von nachbarknoten
    - weit weg für maximierer
    - nach für minimierer
- unterscheidung zwischen maximierer und minimierer

Nicht jeden Knoten sampeln:
- nachbarn zu gesetzten knoten gesondert betrachten
- welche Knoten liegen zwischen den gesetzten knoten