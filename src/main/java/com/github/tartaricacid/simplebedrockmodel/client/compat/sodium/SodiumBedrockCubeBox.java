package com.github.tartaricacid.simplebedrockmodel.client.compat.sodium;

import cn.sh1rocu.touhoulittlemaid.client.ModelMatrices;
import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockCubeBox;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.jellysquid.mods.sodium.client.model.vertex.VertexDrain;
import me.jellysquid.mods.sodium.client.model.vertex.VanillaVertexTypes;
import me.jellysquid.mods.sodium.client.model.vertex.formats.quad.QuadVertexSink;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SodiumBedrockCubeBox extends BedrockCubeBox implements ISodiumVertexWriter {
    public SodiumBedrockCubeBox(float texOffX, float texOffY, float x, float y, float z, float width, float height, float depth, float delta, boolean mirror, float texWidth, float texHeight) {
        super(texOffX, texOffY, x, y, z, width, height, depth, delta, mirror, texWidth, texHeight);
    }

    @Override
    public void compile(PoseStack.Pose pose, Vector3f[] normals, VertexConsumer consumer, int lightmap, int overlay, float r, float g, float b, float a) {
        QuadVertexSink writer = VertexDrain.of(consumer).createSink(VanillaVertexTypes.QUADS);

        Matrix4f matrix4f = ModelMatrices.position(pose.pose());
        prepareVertices(matrix4f);
        prepareNormals(normals);
        int color = (int) (a * 255.0f) << 24 | (int) (b * 255.0f) << 16 | (int) (g * 255.0f) << 8 | (int) (r * 255.0f);

        writer.ensureCapacity(24);

        for (int i = 0; i < NUM_CUBE_FACES; i++) {
            writer.writeQuad(VERTICES[VERTEX_ORDER[i][0]].x, VERTICES[VERTEX_ORDER[i][0]].y, VERTICES[VERTEX_ORDER[i][0]].z,
                    color, uvs[uvOrder[i][1]], uvs[uvOrder[i][2]], lightmap, overlay, NORMALS[i]);

            writer.writeQuad(VERTICES[VERTEX_ORDER[i][1]].x, VERTICES[VERTEX_ORDER[i][1]].y, VERTICES[VERTEX_ORDER[i][1]].z,
                    color, uvs[uvOrder[i][0]], uvs[uvOrder[i][2]], lightmap, overlay, NORMALS[i]);

            writer.writeQuad(VERTICES[VERTEX_ORDER[i][2]].x, VERTICES[VERTEX_ORDER[i][2]].y, VERTICES[VERTEX_ORDER[i][2]].z,
                    color, uvs[uvOrder[i][0]], uvs[uvOrder[i][3]], lightmap, overlay, NORMALS[i]);

            writer.writeQuad(VERTICES[VERTEX_ORDER[i][3]].x, VERTICES[VERTEX_ORDER[i][3]].y, VERTICES[VERTEX_ORDER[i][3]].z,
                    color, uvs[uvOrder[i][1]], uvs[uvOrder[i][3]], lightmap, overlay, NORMALS[i]);

        }

        writer.flush();
    }
}
