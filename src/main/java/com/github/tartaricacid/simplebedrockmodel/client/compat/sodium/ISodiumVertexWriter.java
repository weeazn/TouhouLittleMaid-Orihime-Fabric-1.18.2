package com.github.tartaricacid.simplebedrockmodel.client.compat.sodium;

import org.joml.Vector3f;

public interface ISodiumVertexWriter {

    int[] NORMALS = new int[6];

    default int packNormal(float x, float y, float z) {
        int normX = (int) (x * 127.0f) & 255;
        int normY = (int) (y * 127.0f) & 255;
        int normZ = (int) (z * 127.0f) & 255;

        return (normZ << 16) | (normY << 8) | normX;
    }

    default void prepareNormals(Vector3f[] normals) {
        for (int i = 0; i < normals.length; i++) {
            NORMALS[i] = packNormal(normals[i].x, normals[i].y, normals[i].z);
        }
    }
}
