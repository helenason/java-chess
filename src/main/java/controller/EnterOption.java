package controller;

import java.util.Arrays;

public enum EnterOption {

    NEW("new"),
    OLD("\\d+"),
    ;

    private final String format;

    EnterOption(String format) {
        this.format = format;
    }

    public static EnterOption asEnterOption(String rawEnterOption) {
        return Arrays.stream(values())
                .filter(enterOption -> rawEnterOption.matches(enterOption.format))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] new 또는 방 번호를 입력해주세요."));
    }

    public boolean isNew() {
        return this == NEW;
    }
}
