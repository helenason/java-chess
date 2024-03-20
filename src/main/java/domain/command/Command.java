package domain.command;

public enum Command {

    START,
    MOVE,
    END,
    ;

    public boolean isStart() {
        return this == START;
    }
}