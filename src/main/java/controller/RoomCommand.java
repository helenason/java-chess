package controller;

public enum RoomCommand {

    CREATE(true),
    ENTER(false),
    ;

    private final boolean wantCreate;

    RoomCommand(boolean wantCreate) {
        this.wantCreate = wantCreate;
    }

    public boolean wantCreate() {
        return wantCreate;
    }
}
