package com.client.chat.swing.lib;

public enum Character {
    NEW_LINE(System.lineSeparator()),
    SPACE(" "),
    HASHTAG("#"),
    HYPHEN("-");

    // attributes
    private final String value;

    // constructors
    Character(String value) {
        this.value = value;
    }

    // getters
    public String getValue() {
        return value;
    }
}
