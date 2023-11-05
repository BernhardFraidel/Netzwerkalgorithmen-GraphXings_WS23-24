package GraphXings.Data;

public record Point(Rational x, Rational y) {
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point otherPoint) {
            return Rational.equals(x, otherPoint.x) && Rational.equals(y, otherPoint.y);
        }
        return false;
    }
}
