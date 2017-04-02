package ru.ariadna.misca.channels;

class Channel {
    String name;
    String owner;
    int radius = 0;
    String format = "[%s]%s: %s";
//    boolean crossDimension = true;
//    String permission;

    Channel(String owner, String name) {
        this.name = name;
        this.owner = owner;
    }
}
