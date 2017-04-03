package ru.ariadna.misca.channels;

import java.util.HashSet;
import java.util.Set;

class Channel {
    String name;
    int radius = 0;
    String format = "[%s] %s: %s";
    boolean isLink = false;
    boolean isMuted = false;
    boolean canInvite = false;
    boolean isPublic = false;
    boolean isGlobal = false;
    Set<String> players = new HashSet<>();

    Channel(String name) {
        this.name = name;
    }
}
