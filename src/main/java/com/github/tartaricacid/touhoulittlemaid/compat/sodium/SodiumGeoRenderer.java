package com.github.tartaricacid.touhoulittlemaid.compat.sodium;

import cn.sh1rocu.touhoulittlemaid.client.ModelMatrices;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated.AnimatedGeoBone;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoMesh;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.jellysquid.mods.sodium.client.model.vertex.VertexDrain;
import me.jellysquid.mods.sodium.client.model.vertex.VanillaVertexTypes;
import me.jellysquid.mods.sodium.client.model.vertex.formats.quad.QuadVertexSink;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

class SodiumGeoRenderer {
    static Vector3f C000 = new Vector3f();
    static Vector3f C100 = new Vector3f();
    static Vector3f C110 = new Vector3f();
    static Vector3f C010 = new Vector3f();
    static Vector3f C001 = new Vector3f();
    static Vector3f C101 = new Vector3f();
    static Vector3f C111 = new Vector3f();
    static Vector3f C011 = new Vector3f();
    static Vector3f dx = new Vector3f();
    static Vector3f dy = new Vector3f();
    static Vector3f dz = new Vector3f();
    static Vector3f nx = new Vector3f();
    static Vector3f ny = new Vector3f();
    static Vector3f nz = new Vector3f();

    private static int packUnsafe(float x, float y, float z) {
        int normX = (int) (x * 127.0f) & 255;
        int normY = (int) (y * 127.0f) & 255;
        int normZ = (int) (z * 127.0f) & 255;

        return (normZ << 16) | (normY << 8) | normX;
    }

    static boolean renderCubesOfBone(AnimatedGeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight,
                                     int packedOverlay, float red, float green, float blue, float alpha) {
        QuadVertexSink writer = VertexDrain.of(buffer).createSink(VanillaVertexTypes.QUADS);
        if (bone.isHidden()) {
            return true;
        }
        if (bone.cubesAreHidden()) {
            return true;
        }

        GeoMesh mesh = bone.geoBone().cubes();

        int color = (int) (alpha * 255.0f) << 24 | (int) (blue * 255.0f) << 16 | (int) (green * 255.0f) << 8 | (int) (red * 255.0f);
        for (int i = 0; i < mesh.getCubeCount(); i++) {
            Matrix4f pose = ModelMatrices.position(poseStack.last().pose());
            mesh.position(i).mulPosition(pose, C000);
            mesh.dx(i).mulDirection(pose, dx);
            mesh.dy(i).mulDirection(pose, dy);
            mesh.dz(i).mulDirection(pose, dz);

            C000.add(dx, C100);
            C100.add(dy, C110);
            C000.add(dy, C010);
            C000.add(dz, C001);
            C100.add(dz, C101);
            C110.add(dz, C111);
            C010.add(dz, C011);

            dx.cross(dy, nz).normalize();
            dy.cross(dz, nx).normalize();
            dz.cross(dx, ny).normalize();

            int faces = mesh.faces(i);
            boolean mirrored = (faces & 0b1000000) != 0;
            if ((faces & 0b111111) == 0b111111 && ModelMatrices.position(RenderSystem.getModelViewMatrix()).m32() == 0) {
                if ((C101.x + C000.x) * ny.x + (C101.y + C000.y) * ny.y + (C101.z + C000.z) * ny.z < 0) {
                    faces &= mirrored ? ~0b000010 : ~0b00001;
                }
                if ((C110.x + C011.x) * ny.x + (C110.y + C011.y) * ny.y + (C110.z + C011.z) * ny.z > 0) {
                    faces &= mirrored ? ~0b00001 : ~0b000010;
                }
                if ((C100.x + C010.x) * nz.x + (C100.y + C010.y) * nz.y + (C100.z + C010.z) * nz.z < 0) {
                    faces &= mirrored ? ~0b001000 : ~0b000100;
                }
                if ((C001.x + C111.x) * nz.x + (C001.y + C111.y) * nz.y + (C001.z + C111.z) * nz.z > 0) {
                    faces &= mirrored ? ~0b000100 : ~0b001000;
                }
                if ((C000.x + C011.x) * nx.x + (C000.y + C011.y) * nx.y + (C000.z + C011.z) * nx.z < 0) {
                    faces &= mirrored ? ~0b100000 : ~0b010000;
                }
                if ((C101.x + C110.x) * nx.x + (C101.y + C110.y) * nx.y + (C101.z + C110.z) * nx.z > 0) {
                    faces &= mirrored ? ~0b010000 : ~0b100000;
                }
            } else {
                Matrix3f normal = ModelMatrices.normal(poseStack.last().normal());
                mesh.dx(i).cross(mesh.dy(i), nz);
                mesh.dy(i).cross(mesh.dz(i), nx);
                mesh.dz(i).cross(mesh.dx(i), ny);
                nx.mul(normal).normalize();
                ny.mul(normal).normalize();
                nz.mul(normal).normalize();
            }

            if (mirrored) {
                nx.mul(-1);
                ny.mul(-1);
                nz.mul(-1);
            }

            int normalPX = packUnsafe(nx.x, nx.y, nx.z);
            int normalPY = packUnsafe(ny.x, ny.y, ny.z);
            int normalPZ = packUnsafe(nz.x, nz.y, nz.z);
            int normalNX = packUnsafe(-nx.x, -nx.y, -nx.z);
            int normalNY = packUnsafe(-ny.z, -ny.y, -ny.z);
            int normalNZ = packUnsafe(-nz.x, -nz.y, -nz.z);

            writer.ensureCapacity(24);
            if ((faces & 0b000001) != 0) // DOWN
            {
                writer.writeQuad(C101.x, C101.y, C101.z, color, mesh.downU0(i), mesh.downV1(i), packedLight, packedOverlay, normalNY);

                writer.writeQuad(C001.x, C001.y, C001.z, color, mesh.downU1(i), mesh.downV1(i), packedLight, packedOverlay, normalNY);

                writer.writeQuad(C000.x, C000.y, C000.z, color, mesh.downU1(i), mesh.downV0(i), packedLight, packedOverlay, normalNY);

                writer.writeQuad(C100.x, C100.y, C100.z, color, mesh.downU0(i), mesh.downV0(i), packedLight, packedOverlay, normalNY);

            }
            if ((faces & 0b000010) != 0) // UP
            {
                writer.writeQuad(C110.x, C110.y, C110.z, color, mesh.upU0(i), mesh.upV1(i), packedLight, packedOverlay, normalPY);

                writer.writeQuad(C010.x, C010.y, C010.z, color, mesh.upU1(i), mesh.upV1(i), packedLight, packedOverlay, normalPY);

                writer.writeQuad(C011.x, C011.y, C011.z, color, mesh.upU1(i), mesh.upV0(i), packedLight, packedOverlay, normalPY);

                writer.writeQuad(C111.x, C111.y, C111.z, color, mesh.upU0(i), mesh.upV0(i), packedLight, packedOverlay, normalPY);

            }
            if ((faces & 0b000100) != 0) // NORTH
            {
                writer.writeQuad(C100.x, C100.y, C100.z, color, mesh.northU0(i), mesh.northV1(i), packedLight, packedOverlay, normalNZ);

                writer.writeQuad(C000.x, C000.y, C000.z, color, mesh.northU1(i), mesh.northV1(i), packedLight, packedOverlay, normalNZ);

                writer.writeQuad(C010.x, C010.y, C010.z, color, mesh.northU1(i), mesh.northV0(i), packedLight, packedOverlay, normalNZ);

                writer.writeQuad(C110.x, C110.y, C110.z, color, mesh.northU0(i), mesh.northV0(i), packedLight, packedOverlay, normalNZ);

            }
            if ((faces & 0b001000) != 0) // SOUTH
            {
                writer.writeQuad(C001.x, C001.y, C001.z, color, mesh.southU0(i), mesh.southV1(i), packedLight, packedOverlay, normalPZ);

                writer.writeQuad(C101.x, C101.y, C101.z, color, mesh.southU1(i), mesh.southV1(i), packedLight, packedOverlay, normalPZ);

                writer.writeQuad(C111.x, C111.y, C111.z, color, mesh.southU1(i), mesh.southV0(i), packedLight, packedOverlay, normalPZ);

                writer.writeQuad(C011.x, C011.y, C011.z, color, mesh.southU0(i), mesh.southV0(i), packedLight, packedOverlay, normalPZ);

            }
            if ((faces & 0b010000) != 0) // WEST
            {
                writer.writeQuad(C000.x, C000.y, C000.z, color, mesh.westU0(i), mesh.westV1(i), packedLight, packedOverlay, normalNX);

                writer.writeQuad(C001.x, C001.y, C001.z, color, mesh.westU1(i), mesh.westV1(i), packedLight, packedOverlay, normalNX);

                writer.writeQuad(C011.x, C011.y, C011.z, color, mesh.westU1(i), mesh.westV0(i), packedLight, packedOverlay, normalNX);

                writer.writeQuad(C010.x, C010.y, C010.z, color, mesh.westU0(i), mesh.westV0(i), packedLight, packedOverlay, normalNX);

            }
            if ((faces & 0b100000) != 0) // EAST
            {
                writer.writeQuad(C101.x, C101.y, C101.z, color, mesh.eastU0(i), mesh.eastV1(i), packedLight, packedOverlay, normalPX);

                writer.writeQuad(C100.x, C100.y, C100.z, color, mesh.eastU1(i), mesh.eastV1(i), packedLight, packedOverlay, normalPX);

                writer.writeQuad(C110.x, C110.y, C110.z, color, mesh.eastU1(i), mesh.eastV0(i), packedLight, packedOverlay, normalPX);

                writer.writeQuad(C111.x, C111.y, C111.z, color, mesh.eastU0(i), mesh.eastV0(i), packedLight, packedOverlay, normalPX);

            }

            writer.flush();
        }

        return true;
    }

}
