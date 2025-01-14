package com.client.chat.swing.protocol;

import java.awt.Color;

public enum CustomColors {
    BACKGROUND(new Color(44, 44, 44)),
    SOFT_BACKGROUND(new Color(200, 200, 200)),
    SECONDARY(new Color(53, 53, 53)),
    MAIN_FOREGROUND(Color.WHITE),
    SECONDARY_FOREGROUND(new Color(170, 170, 170)),
    HOVER(new Color(56, 56, 56)),
    BORDER(new Color(32, 32, 32)),
    WINDOW_BORDER(new Color(59, 59, 59)),
    TEXT_BUBBLE_BACKGROUND_OWNER(new Color(0, 92, 75)),
    TEXT_BUBBLE_BACKGROUND_OTHERS(new Color(44, 44, 44)),
    DANGER(new Color(254, 53, 0)),
    PRIMARY(new Color(64, 123, 255));

    private final Color color;

    CustomColors(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
