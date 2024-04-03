package view.mapper.input;

import controller.RoomCommand;
import java.util.Arrays;

public enum RoomCommandInput {

    CREATE(RoomCommand.CREATE, "create"),
    ENTER(RoomCommand.ENTER, "enter"),
    ;

    private final RoomCommand command;
    private final String format;

    RoomCommandInput(RoomCommand command, String format) {
        this.command = command;
        this.format = format;
    }

    public static RoomCommand asCommand(String rawCommand) {
        return Arrays.stream(values())
                .filter(roomCommand -> rawCommand.equals(roomCommand.format))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 올바른 명령어를 입력해주세요."))
                .command;
    }
}
