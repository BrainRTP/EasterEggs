package ru.brainrtp.eastereggs.data;

public enum EggTypes {

    BLOCK("BLOCK"),
    ENTITY("ENTITY");

    final String type;

    EggTypes(String type) {
        this.type = type;
    }

    public String getStringType() {
        return type;
    }
}
