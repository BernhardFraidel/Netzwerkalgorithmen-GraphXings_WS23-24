package GraphXings.Algorithms;

import GraphXings.Data.*;

import java.util.*;

public class BentleyOttmannCrossingCalculator {
    Graph g;
    HashMap<Vertex, Coordinate> vertexCoordinates;
    Queue<Event> eventQueue;
    List<Segment> activeSegments;
    int intersections;

    public BentleyOttmannCrossingCalculator(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates) {
        this.g = g;
        this.vertexCoordinates = vertexCoordinates;
    }

    public int calculateWithUpperBound(int upperBound) {
        return calculate();
    }

    public int calculate() {
        initialize();

        while (!this.eventQueue.isEmpty()) {
            Event event = this.eventQueue.poll();
            Segment segment = event.segments.get(0);
            switch (event.eventType) {
                case SEGMENT_START -> {
                    //insert new segments at correct position
                    int insertionIndex = getIndexForSegmentInsertion(event.point);
                    this.activeSegments.add(insertionIndex, segment);

                    //check for intersection of new segments with lower neighbor
                    Segment lowerNeighbour = this.activeSegments.get(insertionIndex);
                    checkForIntersectionAndAddEvent(lowerNeighbour, segment);

                    //same with upper neighbour
                    try {
                        Segment upperNeighbour = this.activeSegments.get(insertionIndex + 2);
                        checkForIntersectionAndAddEvent(segment, upperNeighbour);
                    } catch (IndexOutOfBoundsException ignored) {
                    }
                }
                case SEGMENT_END -> {
                    //check neighbors for intersection
                    int index = this.activeSegments.indexOf(segment);
                    try {
                        Segment lowerNeighbour = this.activeSegments.get(index - 1);
                        Segment upperNeighbour = this.activeSegments.get(index + 1);
                        checkForIntersectionAndAddEvent(lowerNeighbour, upperNeighbour);
                    } catch (IndexOutOfBoundsException ignored) {
                    }

                    //remove from active segments
                    this.activeSegments.remove(index);
                }
                case INTERSECTION -> {
                    //increment intersection count
                    intersections++;
                    Segment upperSegment = event.segments.get(1);
                    int lowerIndex = this.activeSegments.indexOf(segment);
                    int upperIndex = this.activeSegments.indexOf(upperSegment);

                    //the two segments must be neighbors
                    assert Math.abs(lowerIndex - upperIndex) == 1;

                    //swap intersecting segments (in activeSegments list)
                    Collections.swap(this.activeSegments, lowerIndex, upperIndex);
                }
                default -> {
                }
            }
        }

        return intersections;
    }

    private void checkForIntersectionAndAddEvent(Segment segment1, Segment segment2) {
        try {
            Segment.getIntersection(segment1, segment2).ifPresent(point -> {
                Event newEvent = new Event(point, EventType.INTERSECTION, List.of(segment1, segment2));
                this.eventQueue.add(newEvent);
            });
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    private int getIndexForSegmentInsertion(Point point) {
        int index = 0;
        for (Segment segment : this.activeSegments) {
            Rational y = segment.isVertical() ? segment.getStartY() : Rational.plus(Rational.times(segment.getA(), point.x()), segment.getB());
            index = this.activeSegments.indexOf(segment) + 1;
            if (!Rational.lesserEqual(point.y(), y)) {
                break;
            }
        }
        return index;
    }


    private void initialize() {
        intersections = 0;
        //init queue
        //the queue orders the events after the x point, then after the y point
        //and finally after the direction of the segments in case the events have the same coordinates
        this.eventQueue = new PriorityQueue<>((o1, o2) -> {
            if (!Rational.lesserEqual(o1.point.x(), o2.point.x())) return 1;
            else if (Rational.lesserEqual(o1.point.x(), o2.point.x()) && !Rational.equals(o1.point.x(), o2.point.x()))
                return -1;
            else {
                if (!Rational.lesserEqual(o1.point.y(), o2.point.y())) return 1;
                else if (Rational.lesserEqual(o1.point.y(), o2.point.y()) && !Rational.equals(o1.point.y(), o2.point.y()))
                    return -1;
                else {
                    Segment o1Segment = o1.segments.get(0);
                    Segment o2Segment = o2.segments.get(0);
                    if (o1Segment.getEndY().equals(o2Segment.getEndY())) return 0;
                    else if (Rational.lesserEqual(o1Segment.getEndY(), o2Segment.getEndY())) return -1;
                    else return 1;
                }
            }
        });

        this.activeSegments = new ArrayList<>();

        //for each edge, add events for start and end to eventQueue
        for (Edge edge : g.getEdges()) {
            Coordinate tCoordinate = vertexCoordinates.get(edge.getT());
            Coordinate sCoordinate = vertexCoordinates.get(edge.getS());
            Coordinate start = tCoordinate.getX() < sCoordinate.getX() ? tCoordinate : sCoordinate;
            Coordinate end = start.equals(tCoordinate) ? sCoordinate : tCoordinate;

            Segment segment = new Segment(start, end);
            Event newStartEvent = new Event(new Point(new Rational(start.getX()), new Rational(start.getY())), EventType.SEGMENT_START, List.of(segment));
            Event newEndEvent = new Event(new Point(new Rational(end.getX()), new Rational(end.getY())), EventType.SEGMENT_END, List.of(segment));
            this.eventQueue.add(newStartEvent);
            this.eventQueue.add(newEndEvent);
            //if (!this.eventQueue.contains(newStartEvent)) {
            //    this.eventQueue.add(newStartEvent);
            //}
            //if (!this.eventQueue.contains(newEndEvent)) {
            //    this.eventQueue.add(newEndEvent);
            //}
        }
    }


    private record Event(Point point, EventType eventType, List<Segment> segments) {
    }


    private enum EventType {
        SEGMENT_START,
        SEGMENT_END,
        INTERSECTION
    }
}
