package controller;

public enum Command {

    START(false),
    MOVE(true),
    END(false),
    STATUS(true),
    ;

    private final boolean wantContinue;

    Command(boolean wantContinue) {
        this.wantContinue = wantContinue;
    }

    public boolean isStart() {
        return this == START;
    }

    public boolean isMove() {
        return this == MOVE;
    }

    public boolean isStatus() {
        return this == STATUS;
    }

    public boolean wantContinue() {
        return wantContinue;
    }
}
