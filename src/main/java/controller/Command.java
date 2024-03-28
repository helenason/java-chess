package controller;

public enum Command {

    START,
    MOVE,
    END,
    STATUS,
    ;

    public boolean isStart() {
        return this == START;
    }

    public boolean isEnd() {
        return this == END;
    }

    public boolean isStatus() {
        return this == STATUS;
    }
}
