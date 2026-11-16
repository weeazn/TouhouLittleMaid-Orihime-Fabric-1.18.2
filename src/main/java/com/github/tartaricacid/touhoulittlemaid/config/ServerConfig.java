package com.github.tartaricacid.touhoulittlemaid.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class ServerConfig {
    public static final ForgeConfigSpec.IntValue[] FAVOR_COST = new ForgeConfigSpec.IntValue[3];
    public static final ForgeConfigSpec.IntValue[] FAVOR_HEALTH = new ForgeConfigSpec.IntValue[3];
    public static ForgeConfigSpec SPEC;

    // 客户端需要下载的包
    public static ForgeConfigSpec.ConfigValue<List<String>> CLIENT_PACK_DOWNLOAD_URLS;

    // 开启女仆 AI 耗时检测
    public static ForgeConfigSpec.BooleanValue MAID_AI_TIME_DEBUG;

    // 女仆备份机制
    public static ForgeConfigSpec.IntValue MAID_BACKUP_INTERVAL_SECONDS;
    public static ForgeConfigSpec.IntValue MAID_BACKUP_MAX_COUNT;

    public static int defaultInt(ForgeConfigSpec.IntValue value) {
        ForgeConfigSpec.ValueSpec spec = SPEC.getSpec().get(value.getPath());
        return (Integer) spec.getDefault();
    }

    public static ForgeConfigSpec init() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("The maid pack that the client player needs to download, needs to fill in the URL value of the file");
        builder.comment("Example: [\"https://www.dropbox.com/download/apple.zip\", \"https://www.dropbox.com/download/cat.zip\"]");
        CLIENT_PACK_DOWNLOAD_URLS = builder.define("ClientPackDownloadUrls", Lists.newArrayList());

        builder.comment("When turned on, when the maid AI executes more than 50ms, the entity information of the problem will be logged");
        MAID_AI_TIME_DEBUG = builder.define("MaidAITimeDebug", false);

        builder.comment("The interval time (in seconds) for the maid backup mechanism to back up maid data");
        MAID_BACKUP_INTERVAL_SECONDS = builder.defineInRange("MaidBackupIntervalSeconds", 60 * 3, 5, Integer.MAX_VALUE);

        builder.comment("The maximum number of backups for each player when the maid backup mechanism is enabled");
        MAID_BACKUP_MAX_COUNT = builder.defineInRange("MaidBackupMaxCount", 3, 1, 64);

        builder.push("favorability");
        int[] costs = {64, 128, 192};
        int[] health = {10, 10, 40};
        for (int i = 0; i < costs.length; i++) {
            FAVOR_COST[i] = builder.comment("Additional favor points required to reach this level from the previous level")
                    .defineInRange("Level" + (i + 1) + "Cost", costs[i], 1, 1000000);
            FAVOR_HEALTH[i] = builder.comment("Maximum health gained at this level, in HP (2 HP = 1 heart)")
                    .defineInRange("Level" + (i + 1) + "HealthGain", health[i], 0, 328);
        }
        builder.pop();
        SPEC = builder.build();
        return SPEC;
    }
}