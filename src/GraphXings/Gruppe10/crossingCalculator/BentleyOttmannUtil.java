package GraphXings.Gruppe10.crossingCalculator;

import GraphXings.Data.Rational;
import GraphXings.Data.Segment;
import GraphXings.Gruppe10.data.Point;

import java.util.Optional;

public class BentleyOttmannUtil {
    /**
     * Calculates y = a * x + b
     */
    private static Rational getY(Segment s, Rational x) {
        return Rational.plus(Rational.times(s.getA(), x), s.getB());
    }

    public static Optional<Point> getIntersection(Segment s1, Segment s2) {
        //check if adjacent
        Point startS1 = new Point(s1.getStartX(), s1.getStartY());
        Point endS1 = new Point(s1.getEndX(), s1.getEndY());
        Point startS2 = new Point(s2.getStartX(), s2.getStartY());
        Point endS2 = new Point(s2.getEndX(), s2.getEndY());
        if (startS1.equals(startS2) || startS1.equals(endS2) || endS1.equals(startS2) || endS1.equals(endS2)) {
            return Optional.empty();
        }

        if (!s1.isVertical() && !s2.isVertical())
        {
            if (!Rational.equals(s1.getA(),s2.getA()))
            {
                //both segments are not vertical and don't have the same slope
                Rational x = Rational.dividedBy(Rational.minus(s2.getB(), s1.getB()), Rational.minus(s1.getA(), s2.getA()));
                if (Rational.lesserEqual(s1.getStartX(), x) && Rational.lesserEqual(s2.getStartX(), x) && Rational.lesserEqual(x, s1.getEndX()) && Rational.lesserEqual(x, s2.getEndX())) {
                    return Optional.of(new Point(x, getY(s1, x)));
                } else {
                    return Optional.empty();
                }
            }
            else
            {
                if (Rational.equals(s1.getB(),s2.getB()))
                {
                    //both segments are not vertical, have the same slope and the same y-offset (i.e. could overlap)
                    if ((Rational.lesserEqual(s1.getStartX(), s2.getStartX()) && Rational.lesserEqual(s2.getStartX(), s1.getEndX()))) {
                        return Optional.of(new Point(s2.getStartX(), s2.getStartY()));
                    }
                    if ((Rational.lesserEqual(s2.getStartX(), s1.getStartX()) && Rational.lesserEqual(s1.getStartX(), s2.getEndX()))) {
                        return Optional.of(new Point(s1.getStartX(), s1.getStartY()));
                    } else {
                        return Optional.empty();
                    }
                }
                else
                {
                    //both segments are not vertical, have the same slope and not the same y-offset (i.e. are parallel)
                    return Optional.empty();
                }
            }
        }
        if (!s1.isVertical())
        {
            Segment swap = s1;
            s1 = s2;
            s2 = swap;
        }
        if (!s2.isVertical())
        {
            //only s1 is vertical
            Rational y = Rational.plus(Rational.times(s2.getA(),s1.getStartX()),s2.getB());
            if (Rational.lesserEqual(s2.getStartX(),s1.getStartX()) && Rational.lesserEqual(s1.getStartX(),s2.getEndX()) && Rational.lesserEqual(s1.getStartY(),y) && Rational.lesserEqual(y,s1.getEndY())) {
                return Optional.of(new Point(s1.getStartX(), y));
            }
            return Optional.empty();
        }
        else
        {
            //both are vertical
            if (!s1.getStartX().equals(s2.getStartX()))
            {
                return Optional.empty();
            }
            else
            {
                if (Rational.lesserEqual(s2.getStartY(),s1.getStartY()))
                {
                    Segment swap = s1;
                    s1 = s2;
                    s2 = swap;
                }
                if (Rational.lesserEqual(s2.getStartY(),s1.getEndY())) {
                    return Optional.of(new Point(s1.getStartX(), s2.getStartY()));
                }
                return Optional.empty();
            }
        }
    }
}
