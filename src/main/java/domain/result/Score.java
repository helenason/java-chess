package domain.result;

public class Score {

    private final double value;

    public Score(double value) {
        this.value = value;
    }

    public double calculateScore(int count) {
        return value * count;
    }
}
