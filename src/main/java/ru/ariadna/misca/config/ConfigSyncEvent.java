package ru.ariadna.misca.config;

import java.io.Serializable;
import java.util.HashMap;

class ConfigSyncEvent {
    HashMap<String, Serializable> configs = new HashMap<>();
}
