package com.github.tartaricacid.touhoulittlemaid.datapack.resources;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.datapack.BoardStateData;
import com.github.tartaricacid.touhoulittlemaid.datapack.pojo.BoardStateRecord;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.GsonHelper;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BoardStateDataReloadListener implements ResourceManagerReloadListener, IdentifiableResourceReloadListener {
    private static final ResourceLocation CHESS_PATH = new ResourceLocation(TouhouLittleMaid.MOD_ID, "board_states/chess.json");
    private static final ResourceLocation XIANGQI_PATH = new ResourceLocation(TouhouLittleMaid.MOD_ID, "board_states/xiangqi.json");
    private static final ResourceLocation GOMOKU_PATH = new ResourceLocation(TouhouLittleMaid.MOD_ID, "board_states/gomoku.json");

    private static final ResourceLocation ID = new ResourceLocation(TouhouLittleMaid.MOD_ID, "board_states_reload");

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        // 先清除旧数据
        BoardStateData.clear();
        // 再逐层读取新数据，进行合并加载
        resourceManager.listPacks().forEach(packResources -> {
            readData(packResources, CHESS_PATH, BoardStateData::addChessRecords);
            readData(packResources, XIANGQI_PATH, BoardStateData::addXiangqiRecords);
            readData(packResources, GOMOKU_PATH, BoardStateData::addGomokuRecords);
        });
    }

    private static void readData(PackResources packResources, ResourceLocation path, Consumer<List<BoardStateRecord>> adder) {
        if (!packResources.hasResource(PackType.SERVER_DATA, path)) {
            return;
        }
        try (InputStream inputStream = packResources.getResource(PackType.SERVER_DATA, path); InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            List<BoardStateRecord> records = new ArrayList<>();
            for (JsonElement element : JsonParser.parseReader(reader).getAsJsonArray()) {
                JsonObject record = element.getAsJsonObject();
                List<String> tags = new ArrayList<>();
                for (JsonElement tag : GsonHelper.getAsJsonArray(record, "tags")) tags.add(tag.getAsString());
                JsonObject display = GsonHelper.getAsJsonObject(record, "display");
                records.add(new BoardStateRecord(tags, new BoardStateRecord.Display(
                        GsonHelper.getAsString(display, "description", ""), GsonHelper.getAsString(display, "author", "")),
                        GsonHelper.getAsString(record, "data"), GsonHelper.getAsInt(record, "weight", 0)));
            }
            if (!records.isEmpty()) {
                adder.accept(records);
            }
        } catch (Exception e) {
            TouhouLittleMaid.LOGGER.error("Failed to load board state data from {}", path, e);
        }
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }
}
