package com.github.tartaricacid.touhoulittlemaid.datapack.resources;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.ai.agent.skill.SkillBean;
import com.github.tartaricacid.touhoulittlemaid.ai.agent.skill.SkillInstance;
import com.github.tartaricacid.touhoulittlemaid.ai.agent.skill.SkillLoader;
import com.github.tartaricacid.touhoulittlemaid.ai.agent.skill.SkillParser;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkillsDataReloadListener implements ResourceManagerReloadListener, IdentifiableResourceReloadListener {
    private static final String SKILLS_PATH = "skills";

    private static final Pattern SKILL_FILE_REG = Pattern.compile("skills/([a-z0-9\\-_]+)/skill\\.md");
    private static final Pattern REFERENCES_FILE_REG = Pattern.compile("skills/([a-z0-9\\-_]+)/references/([a-z0-9\\-_]+\\.md)");

    public static final ResourceLocation ID = new ResourceLocation(TouhouLittleMaid.MOD_ID, "skills_data");

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        Map<String, ResourceLocation> skills = Maps.newLinkedHashMap();
        Map<String, Map<String, ResourceLocation>> references = Maps.newLinkedHashMap();

        resourceManager.listResources(SKILLS_PATH, name -> name.endsWith(".md")).forEach(location -> {
            if (!TouhouLittleMaid.MOD_ID.equals(location.getNamespace())) {
                return;
            }
            String path = location.getPath();

            // 提取 skill.md
            Matcher skillMatcher = SKILL_FILE_REG.matcher(path);
            if (skillMatcher.find()) {
                skills.put(skillMatcher.group(1), location);
                return;
            }

            // 提取 references 下的 md 文件
            Matcher referencesMatcher = REFERENCES_FILE_REG.matcher(path);
            if (referencesMatcher.find()) {
                String key = referencesMatcher.group(1);
                String name = referencesMatcher.group(2);
                references.computeIfAbsent(key, k -> Maps.newLinkedHashMap()).put(name, location);
            }
        });

        // 遍历加载
        Map<String, SkillInstance> loadedSkills = Maps.newLinkedHashMap();
        skills.forEach((skillName, skillRes) -> {
            var ref = references.getOrDefault(skillName, Map.of());
            SkillInstance skillInstance = loadSkills(resourceManager, skillName, skillRes, ref);
            if (skillInstance != null) {
                loadedSkills.put(skillInstance.name(), skillInstance);
            }
        });

        SkillLoader.loadSkillFromDatapack(loadedSkills);
    }

    @Nullable
    private static SkillInstance loadSkills(ResourceManager manager, String fileName, ResourceLocation skillRes, Map<String, ResourceLocation> referencesRes) {
        try (Resource resource = manager.getResource(skillRes)) {
            Pair<SkillBean, String> result = SkillParser.parse(resource.getInputStream(), fileName);
            if (result == null) {
                return null;
            }

            SkillBean header = result.getLeft();
            String body = result.getRight();

            if (referencesRes.isEmpty()) {
                return new SkillInstance(
                        header.getName().trim(),
                        header.getDescription().trim(),
                        header.getMetadata(),
                        body, Map.of()
                );
            }

            Map<String, String> references = Maps.newLinkedHashMap();
            referencesRes.forEach((name, res) -> {
                try (Resource refResource = manager.getResource(res)) {
                    String content = IOUtils.toString(refResource.getInputStream(), StandardCharsets.UTF_8);
                    references.put(name, content);
                } catch (Exception e) {
                    TouhouLittleMaid.LOGGER.warn("Failed to read reference file {} for skill {}, skipping this reference. Error: {}",
                            name, fileName, e.getMessage());
                }
            });

            return new SkillInstance(
                    header.getName().trim(),
                    header.getDescription().trim(),
                    header.getMetadata(),
                    body, ImmutableMap.copyOf(references)
            );
        } catch (Exception e) {
            TouhouLittleMaid.LOGGER.warn("Failed to load skill datapack resource {}", fileName, e);
            return null;
        }
    }
}