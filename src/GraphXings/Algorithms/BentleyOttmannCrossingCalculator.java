package GraphXings.Algorithms;

import GraphXings.Data.*;

import java.util.*;

public class BentleyOttmannCrossingCalculator {
    Graph g;
    HashMap<Vertex, Coordinate> vertexCoordinates;
    Queue<Event> eventQueue;
    List<Segment> activeSegments;

    public BentleyOttmannCrossingCalculator(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates) {
        this.g = g;
        this.vertexCoordinates = vertexCoordinates;
    }

    public int computeWithUpperBound(int upperBound) {
        return 0;
    }

    public int compute() {
        initialize();

        while (!this.eventQueue.isEmpty()) {
            Event event = this.eventQueue.poll();
            switch (event.eventType) {
                case SEGMENT_START:
                    //insert new segment at correct position
                    int index = getIndexForSegmentInsertion(event.coordinate.getX(), event.coordinate.getY());
                    this.activeSegments.add(index, event.segment);


                    //check new segment for intersection with neighbors
                    Segment lowerNeighbour = this.activeSegments.get(index);
                    if (Segment.intersect(event.segment, lowerNeighbour)) {
                        //TODO: get intersection coordinates
                        Coordinate intersectionCoordinate = new Coordinate(0,0);
                        Event newEvent = new Event(intersectionCoordinate, EventType.INTERSECTION, ) //TODO: two segments for this event?
                    }


                    //TODO: same with upper neighbour
                    try {
                        Segment upperNeighbour = this.activeSegments.get(index + 2);
                    } catch (IndexOutOfBoundsException ignored) {
                    }


                    break;
                case SEGMENT_END:
                    //TODO: delete from active segments and maybe check new neighbours for intersection?
                    break;
                case INTERSECTION:
                    //TODO: increment intersection count and swap intersecting segments (in activeSegments list)
                    break;
                default:
                    break;
            }
        }

        return 0;
    }

    private int getIndexForSegmentInsertion(int segmentX, int segmentY) {
        int index = 0;
        for (Segment segment : this.activeSegments) {
            Rational y = Rational.plus(Rational.times(segment.getA(), new Rational(segmentX)), segment.getB());
            index = this.activeSegments.indexOf(segment) + 1;
            if (!Rational.lesserEqual(new Rational(segmentY), y)) {
                break;
            }
        }
        return index;
    }



    private void initialize() {
        //init queue
        //this.eventQueue = new PriorityQueue<>(Comparator.comparingInt(o -> o.coordinate.getX()));
        this.eventQueue = new PriorityQueue<>((o1, o2) -> {
            if (o1.coordinate.getX() > o2.coordinate.getX()) return 1;
            else if (o1.coordinate.getX() < o2.coordinate.getX()) return -1;
            else {
                if (o1.coordinate.getY() > o2.coordinate.getY()) return 1;
                else if (o1.coordinate.getY() < o2.coordinate.getY()) return -1;
                else {
                    if (o1.segment.getEndY().equals(o2.segment.getEndY())) return 0;
                    else if (Rational.lesserEqual(o1.segment.getEndY(), o2.segment.getEndY())) return -1;
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
            Event newStartEvent = new Event(start, EventType.SEGMENT_START, segment);
            Event newEndEvent = new Event(end, EventType.SEGMENT_END, segment);
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

    private List<Event> findIntersections(List<LineSegment> segments) {
        List<Event> intersections = new ArrayList<>();

        // Create a priority queue for events (endpoints and intersection points).
        PriorityQueue<Event> eventQueue = new PriorityQueue<>();

        // Create a binary search tree (TreeMap) to maintain the order of active segments.
        TreeMap<Integer, LineSegment> activeSegments = new TreeMap<>();

        // Add segment endpoints as events to the priority queue.
        for (LineSegment segment : segments) {
            eventQueue.add(segment.start);
            eventQueue.add(segment.end);
        }

        while (!eventQueue.isEmpty()) {
            Event event = eventQueue.poll();

            // Handle the event based on its type (start, end, or intersection).
            if (event == event.start) {
                // Handle a start point event.
                activeSegments.put(event.start.y, segment);
            } else if (event == event.end) {
                // Handle an end point event.
                activeSegments.remove(event.start.y);
            } else {
                // Handle an intersection event.
                intersections.add(event);
                // Swap the two segments in the binary search tree.
                LineSegment segment1 = activeSegments.get(event.start.y);
                LineSegment segment2 = activeSegments.higherEntry(event.start.y).getValue();
                activeSegments.remove(event.start.y);
                activeSegments.remove(event.start.y); // Remove both segments.
                activeSegments.put(event.start.y, segment2);
                activeSegments.put(event.start.y, segment1);
            }
        }

        return intersections;
    }

    public static void main(String[] args) {
        // Create a list of line segments.
        List<LineSegment> segments = new ArrayList<>();
        // Add your line segments to the list.

        List<Event> intersections = findIntersections(segments);
        // Process the intersections found.
    }

    private record Event(Coordinate coordinate, EventType eventType, Segment segment) {
    }

    private enum EventType {
        SEGMENT_START,
        SEGMENT_END,
        INTERSECTION
    }

    class LineSegment {
        Event start, end;

        public LineSegment(Event start, Event end) {
            this.start = start;
            this.end = end;
        }

        // Implement a method to check for intersection with another line segment.
        // You'll need to compare slopes and use 2D geometry algorithms here.
    }
}
