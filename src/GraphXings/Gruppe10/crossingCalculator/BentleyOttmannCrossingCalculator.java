package GraphXings.Gruppe10.crossingCalculator;

import GraphXings.Data.*;
import GraphXings.Gruppe10.data.Point;

import java.util.*;
import java.util.stream.Collectors;

public class BentleyOttmannCrossingCalculator {
    Graph g;
    HashMap<Vertex, Coordinate> vertexCoordinates;
    Queue<Event> eventQueue;
    List<Event> processedEvents;
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

        //main loop
        while (!this.eventQueue.isEmpty()) {
            Event event = this.eventQueue.poll();
            this.processedEvents.add(event);
            Segment segment = event.segments.get(0);
            switch (event.eventType) {
                case SEGMENT_START -> {
                    //insert new segments at correct position
                    int insertionIndex = getIndexForSegmentInsertion(event.point, segment.getA());
                    if (insertionIndex != -1) {
                        this.activeSegments.add(insertionIndex, segment);
                    } else {
                        this.activeSegments.add(segment);
                        insertionIndex = this.activeSegments.indexOf(segment);
                    }

                    //check for intersection of new segment with lower and upper neighbour
                    checkForIntersectionWithLowerNeighbour(segment, insertionIndex, event.point.x());
                    checkForIntersectionWithUpperNeighbour(segment, insertionIndex, event.point.x());
                }
                case SEGMENT_END -> {
                    //check neighbors for intersection
                    int index = this.activeSegments.indexOf(segment);
                    try {
                        Segment lowerNeighbour = this.activeSegments.get(index - 1);
                        Segment upperNeighbour = this.activeSegments.get(index + 1);
                        checkForIntersectionAndAddEvent(lowerNeighbour, upperNeighbour, event.point.x());
                    } catch (IndexOutOfBoundsException ignored) {
                    }

                    //remove from active segments
                    try {
                        this.activeSegments.remove(index);
                    } catch (IndexOutOfBoundsException ignored) {

                    }
                }
                case INTERSECTION -> {
                    Segment upperSegment = event.segments.get(1);
                    //edge case: multiple intersections at the same point
                    List<Event> intersectionEventsAtSamePoint = new ArrayList<>(this.eventQueue
                            .stream()
                            .filter(event1 -> event1.eventType == EventType.INTERSECTION && event1.point.equals(event.point))
                            .toList());
                    if (!intersectionEventsAtSamePoint.isEmpty()) {
                        //swap the segments
                        Set<Segment> segmentsIntersectingAtSamePoint = intersectionEventsAtSamePoint
                                .stream()
                                .flatMap(event1 -> event1.segments.stream())
                                .collect(Collectors.toSet());
                        segmentsIntersectingAtSamePoint.add(segment);
                        segmentsIntersectingAtSamePoint.add(upperSegment);

                        Set<Integer> indicesOfSegmentsIntersectingAtSamePoint = segmentsIntersectingAtSamePoint
                                .stream()
                                .map(segment1 -> this.activeSegments.indexOf(segment1))
                                .collect(Collectors.toSet());

                        //remove the intersection events whose segments already intersected at another point
                        // i.e. overlapping segments
                        Set<Event> eventsForSegmentsAlreadyProcessedAtAnotherPoint = new HashSet<>();
                        for (Segment segmentIntersecting : segmentsIntersectingAtSamePoint) {
                            Set<Event> otherEventsContainingSegment = processedEvents
                                    .stream()
                                    .filter(event1 -> (!event1.equals(event) && event1.eventType == EventType.INTERSECTION && event1.segments.contains(segmentIntersecting)))
                                    .collect(Collectors.toSet());
                            for (Event otherEvent : otherEventsContainingSegment) {
                                List<Segment> segmentsOfOtherEvent = new ArrayList<>(otherEvent.segments);
                                segmentsOfOtherEvent.remove(segmentIntersecting);
                                Segment otherSegment = segmentsOfOtherEvent.get(0);
                                if (segmentsIntersectingAtSamePoint.contains(otherSegment)) {
                                    eventsForSegmentsAlreadyProcessedAtAnotherPoint.add(otherEvent);
                                }
                            }
                        }

                        //TODO: remove duplicates from eventsForSegmentsAlreadyProcessedAtAnotherPoint?


                        //calculate n choose 2 where n is the number of segments intersecting in this point
                        int n = indicesOfSegmentsIntersectingAtSamePoint.size();
                        intersections += ((((n - 1) * n) / 2) - eventsForSegmentsAlreadyProcessedAtAnotherPoint.size());
                        int min = indicesOfSegmentsIntersectingAtSamePoint.stream().min(Integer::compareTo).orElse(-1);
                        int max = indicesOfSegmentsIntersectingAtSamePoint.stream().max(Integer::compareTo).orElse(-1);
                        assert min != -1 && max != -1;
                        Collections.reverse(this.activeSegments.subList(min, max + 1));

                        //check for intersections of outer segments
                        checkForIntersectionWithLowerNeighbour(this.activeSegments.get(min), min, event.point.x());
                        checkForIntersectionWithUpperNeighbour(this.activeSegments.get(max), max, event.point.x());

                        //remove all intersections at this point from the eventQueue
                        //(this event is already polled from the queue)
                        this.eventQueue.removeAll(intersectionEventsAtSamePoint);
                        this.processedEvents.addAll(intersectionEventsAtSamePoint);
                        break;
                    }

                    //increment intersection count
                    intersections++;
                    int lowerIndex = this.activeSegments.indexOf(segment);
                    int upperIndex = this.activeSegments.indexOf(upperSegment);

                    //the two segments must be neighbors
                    assert Math.abs(lowerIndex - upperIndex) == 1 : "intersection not between neighbours!";

                    //swap intersecting segments (in activeSegments list)
                    try {
                        Collections.swap(this.activeSegments, lowerIndex, upperIndex);
                    } catch (IndexOutOfBoundsException ignored) {
                    }

                    //check for intersections with new neighbours
                    checkForIntersectionWithLowerNeighbour(upperSegment, lowerIndex, event.point.x());
                    checkForIntersectionWithUpperNeighbour(segment, upperIndex, event.point.x());
                }
                default -> {
                }
            }
        }

        return intersections;
    }

    private void checkForIntersectionWithLowerNeighbour(Segment segment, int index, Rational minX) {
        try {
            Segment lowerNeighbour = this.activeSegments.get(index - 1);
            checkForIntersectionAndAddEvent(lowerNeighbour, segment, minX);
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    private void checkForIntersectionWithUpperNeighbour(Segment segment, int index, Rational minX) {
        try {
            Segment upperNeighbour = this.activeSegments.get(index + 1);
            checkForIntersectionAndAddEvent(segment, upperNeighbour, minX);
        } catch (IndexOutOfBoundsException ignored) {
        }
    }


    private void checkForIntersectionAndAddEvent(Segment segment1, Segment segment2, Rational minX) {
        BentleyOttmannUtil.getIntersection(segment1, segment2).ifPresent(point -> {
            Event newEvent = new Event(point, EventType.INTERSECTION, List.of(segment1, segment2));
            if (!this.eventQueue.contains(newEvent) && (!Rational.lesserEqual(point.x(), minX) || Rational.equals(point.x(), minX))
                    && !this.processedEvents.contains(newEvent)) {
                this.eventQueue.add(newEvent);
            }
        });
    }

    private int getIndexForSegmentInsertion(Point point, Rational slope) {
        int index = 0;
        boolean found = false;
        for (Segment segment : this.activeSegments) {
            Rational y = segment.isVertical() ? segment.getStartY() : Rational.plus(Rational.times(segment.getA(), point.x()), segment.getB());
            index = this.activeSegments.indexOf(segment);
            //shift if event point is less than y of other segment
            if (Rational.lesserEqual(point.y(), y)) {
                //if equal to y of other segment:
                // decide dependent of slope s.t. touching intersection event swaps the two segments correctly
                if (Rational.equals(point.y(), y)) {
                    //invert insertion behavior if segments are adjacent because there won't be
                    // an insertion event that swaps them
                    boolean adjacent = (point.equals(new Point(segment.getStartX(), segment.getStartY()))
                            || point.equals(new Point(segment.getEndX(), segment.getEndY())));
                    if (slope != null && (segment.getA() == null || Rational.lesserEqual(slope, segment.getA()))) {
                        //slope of segment to be inserted is less (or equal) than slope of other segment
                        // so new segment gets inserted after other segment
                        if (!adjacent) {
                            index++;
                        }
                    } else if (adjacent) {
                        index++;
                    }
                }
                found = true;
                break;
            }
        }
        //new segment shall be inserted at the end
        index = found ? index : -1;
        return index;
    }


    private void initialize() {
        intersections = 0;
        //init queue
        //the queue orders the events after the following priority:
        // 1. the x coordinate of the points
        // 2. the y coordinate of the point
        // 3. the event type (INTERSECTION -> START -> END)
        // 4. the slope of the segments
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
                    else {
                        if (o1.eventType == EventType.INTERSECTION) return -1;
                        else if (o2.eventType == EventType.INTERSECTION) return 1;
                        else {
                            Segment o1Segment = o1.segments.get(0);
                            Segment o2Segment = o2.segments.get(0);
                            if (o1Segment.isVertical()) return -1;
                            else if (o2Segment.isVertical()) return 1;
                            else if (o1Segment.getA().equals(o2Segment.getA())) return 0;
                            else if (Rational.lesserEqual(o1Segment.getA(), o2Segment.getA())) return -1;
                            else return 1;
                        }
                    }
                }
            }
        });
        this.processedEvents = new ArrayList<>();
        this.activeSegments = new ArrayList<>();

        //for each edge, add events for start and end to eventQueue
        for (Edge edge : g.getEdges()) {
            Coordinate tCoordinate = vertexCoordinates.get(edge.getT());
            Coordinate sCoordinate = vertexCoordinates.get(edge.getS());
            Coordinate start;
            if (tCoordinate.getX() < sCoordinate.getX() || (tCoordinate.getX() == sCoordinate.getX() && tCoordinate.getY() < sCoordinate.getY())) {
                start = tCoordinate;
            } else {
                start = sCoordinate;
            }
            Coordinate end = start.equals(tCoordinate) ? sCoordinate : tCoordinate;

            Segment segment = new Segment(start, end);
            Event newStartEvent = new Event(new Point(new Rational(start.getX()), new Rational(start.getY())), EventType.SEGMENT_START, List.of(segment));
            Event newEndEvent = new Event(new Point(new Rational(end.getX()), new Rational(end.getY())), EventType.SEGMENT_END, List.of(segment));
            this.eventQueue.add(newStartEvent);
            this.eventQueue.add(newEndEvent);
        }
    }


    private record Event(Point point, EventType eventType, List<Segment> segments) {
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Event event) {
                return event.eventType == this.eventType
                        && new HashSet<>(event.segments).equals(new HashSet<>(this.segments))
                        && event.point.equals(this.point);
            }
            return false;
        }
    }


    private enum EventType {
        SEGMENT_START,
        SEGMENT_END,
        INTERSECTION
    }
}
