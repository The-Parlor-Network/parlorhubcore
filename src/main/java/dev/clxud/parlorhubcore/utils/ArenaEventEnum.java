package dev.clxud.parlorhubcore.utils;

public enum ArenaEventEnum {
    TOP_THE_BAR,
    KING_OF_THE_HILL,
    BAD_BLOOD;

    public String getFormattedName() {
        return switch (this) {
            case TOP_THE_BAR -> "Top The Bar";
            case KING_OF_THE_HILL -> "King Of The Hill";
            case BAD_BLOOD -> "Bad Blood";
        };
    }
}
