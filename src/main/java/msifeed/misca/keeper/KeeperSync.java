package msifeed.misca.keeper;

import com.google.gson.Gson;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import msifeed.misca.charsheet.CharEffort;
import msifeed.misca.charsheet.CharResource;
import msifeed.misca.charsheet.CharSkill;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.combat.CharAttribute;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.concurrent.TimeUnit;

public enum KeeperSync {
    INSTANCE;

    static final Logger LOG = LogManager.getLogger("Misca-Keeper");
    private MongoCollection<KeeperCharsheet> sheets;

    public static void reload() {
        if (KeeperConfig.disabled) return;

        final String conn = String.format("mongodb://%s:%s@%s:%d/%s",
                KeeperConfig.username, KeeperConfig.password,
                KeeperConfig.host, KeeperConfig.port,
                KeeperConfig.database);

        try {
            LOG.info("Try to connect to Keeper DB...");

            final MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(conn))
                    .applyToConnectionPoolSettings(builder -> builder.maxWaitTime(5, TimeUnit.SECONDS))
                    .codecRegistry(CodecRegistries.fromProviders(
                            PojoCodecProvider.builder()
                                    .register(KeeperCharsheet.class)
                                    .build(),
                            new ValueCodecProvider()
                    ))
                    .build();
            INSTANCE.sheets = MongoClients.create(settings)
                    .getDatabase(KeeperConfig.database)
                    .getCollection(KeeperConfig.collection, KeeperCharsheet.class);
            LOG.info("Connection to Keeper DB is successful");
        } catch (Exception e) {
            LOG.error("Failed to connect to Keeper DB", e);
        }
    }

    public void sync(EntityPlayerMP player) {
        if (sheets == null) return;

        final String username = player.getGameProfile().getName();
        LOG.info("Sync {} with Keeper DB...", username);

        final KeeperCharsheet sheet = sheets.find(Filters.eq("character", username)).first();
        if (sheet == null) {
            LOG.warn("Player entry is not found");
            return;
        }

        LOG.info("Found entry: " + new Gson().toJson(sheet));

        final ICharsheet cs = CharsheetProvider.get(player);
        for (CharResource key : CharResource.values())
            cs.resources().set(key, sheet.special_stats.getOrDefault(key.name(), 0));
        for (CharSkill key : CharSkill.values())
            cs.skills().set(key, sheet.skills.getOrDefault(key.name(), 0));
        for (CharEffort key : CharEffort.values())
            cs.effortPools().set(key, sheet.efforts.getOrDefault(key.name(), 0));
        for (CharAttribute key : CharAttribute.values())
            key.setBase(player, sheet.attributes.getOrDefault(key.name(), 0));
    }
}
