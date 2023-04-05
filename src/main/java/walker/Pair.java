package walker;

public class Pair<X, Y> {
    private final X x;
    private final Y y;

    public Pair(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public X getX() {
        return x;
    }

    public Y getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Pair<?, ?> pair = (Pair<?, ?>) obj;
        if (!x.equals(pair.x)) {
            return false;
        }
        return y.equals(pair.y);
    }

    @Override
    public int hashCode() {
        int result = x.hashCode();
        result = 31 * result + y.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "walker.Pair{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public static <X, Y> Pair<X, Y> of(X x, Y y) {
        return new Pair<>(x, y);
    }
}
