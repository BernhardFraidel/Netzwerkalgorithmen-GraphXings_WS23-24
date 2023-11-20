package GraphXings.solutions.crossingCalculator;

import GraphXings.Data.*;
import GraphXings.solutions.data.Point;

import java.util.*;

public class BentleyOttmannCrossingCalculatorLite {
    Graph g;
    HashMap<Vertex, Coordinate> vertexCoordinates;
    Queue<Event> eventQueue;
    Set<Edge> activeEdges;
    int intersections;

    public BentleyOttmannCrossingCalculatorLite(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates) {
        this.g = g;
        this.vertexCoordinates = vertexCoordinates;
    }

    public int calculateWithUpperBound(int upperBound) {
        return calculate();
    }

    public int calculate() {
        initialize();

        //main loop
        while (!this.eventQueue.isEmpty()) {
            Event event = this.eventQueue.poll();
            Edge edge = event.edge;
            switch (event.eventType) {
                case SEGMENT_START -> {
                    //check for intersections with active segments
                    checkForIntersectionsWithNewEdge(edge);
                    //insert new segment
                    this.activeEdges.add(edge);
                }
                case SEGMENT_END -> this.activeEdges.remove(edge);
                default -> {
                }
            }
        }
        return intersections;
    }

    private void checkForIntersectionsWithNewEdge(Edge edge) {
        for (Edge otherEdge : this.activeEdges) {
            if (edge.isAdjacent(otherEdge)
                    || edge.equals(otherEdge)
                    || !vertexCoordinates.containsKey(edge.getS())
                    || !vertexCoordinates.containsKey(edge.getT())
                    || !vertexCoordinates.containsKey(otherEdge.getS())
                    || !vertexCoordinates.containsKey(otherEdge.getT())) continue;

            Segment s1 = new Segment(vertexCoordinates.get(edge.getS()),vertexCoordinates.get(edge.getT()));
            Segment s2 = new Segment(vertexCoordinates.get(otherEdge.getS()),vertexCoordinates.get(otherEdge.getT()));
            if (Segment.intersect(s1, s2)) {
                intersections++;
            }
        }
    }

    private void initialize() {
        intersections = 0;
        //init queue
        //the queue orders the events after the following priority:
        // 1. the x coordinate of the points
        // 2. the y coordinate of the point
        // 3. the event type (START -> END)
        this.eventQueue = new PriorityQueue<>((o1, o2) -> {
            if (!Rational.lesserEqual(o1.point.x(), o2.point.x())) return 1;
            else if (Rational.lesserEqual(o1.point.x(), o2.point.x()) && !Rational.equals(o1.point.x(), o2.point.x()))
                return -1;
            else {
                if (!Rational.lesserEqual(o1.point.y(), o2.point.y())) return 1;
                else if (Rational.lesserEqual(o1.point.y(), o2.point.y()) && !Rational.equals(o1.point.y(), o2.point.y()))
                    return -1;
                else {
                    if (o1.eventType == EventType.SEGMENT_START) return -1;
                    else if (o2.eventType == EventType.SEGMENT_START) return 1;
                    else return 0;
                }
            }
        });
        this.activeEdges = new HashSet<>();

        //for each edge, add events for start and end to eventQueue
        for (Edge edge : g.getEdges()) {
            if (!vertexCoordinates.containsKey(edge.getS()) || !vertexCoordinates.containsKey(edge.getT())) continue;
            Coordinate tCoordinate = vertexCoordinates.get(edge.getT());
            Coordinate sCoordinate = vertexCoordinates.get(edge.getS());
            Coordinate start;
            if (tCoordinate.getX() < sCoordinate.getX() || (tCoordinate.getX() == sCoordinate.getX() && tCoordinate.getY() < sCoordinate.getY())) {
                start = tCoordinate;
            } else {
                start = sCoordinate;
            }
            Coordinate end = start.equals(tCoordinate) ? sCoordinate : tCoordinate;

            Event newStartEvent = new Event(new Point(new Rational(start.getX()), new Rational(start.getY())), EventType.SEGMENT_START, edge);
            Event newEndEvent = new Event(new Point(new Rational(end.getX()), new Rational(end.getY())), EventType.SEGMENT_END, edge);
            this.eventQueue.add(newStartEvent);
            this.eventQueue.add(newEndEvent);
        }
    }


    private record Event(Point point, EventType eventType, Edge edge) {
    }


    private enum EventType {
        SEGMENT_START,
        SEGMENT_END
    }
}
