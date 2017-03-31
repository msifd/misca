package ru.ariadna.misca;

import java.io.File;

public class MiscaConfig {
    public final File config_dir;

    MiscaConfig(File global_config_dir) {
        this.config_dir = new File(global_config_dir, "misca");
        this.config_dir.mkdirs();
    }
}
