package cn.sh1rocu.touhoulittlemaid.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import org.joml.Quaternionfc;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;

/** Convert a Minecraft pose to the column-major matrices used by the bundled model meshes. */
public final class ModelMatrices {
    private static final ThreadLocal<ModelMatrices> LOCAL = ThreadLocal.withInitial(ModelMatrices::new);
    private final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
    private final org.joml.Matrix4f position = new org.joml.Matrix4f();
    private final org.joml.Matrix3f normal = new org.joml.Matrix3f();

    private ModelMatrices() {}

    /** A transient view, valid until the next conversion of a position matrix on this thread. */
    public static org.joml.Matrix4f position(com.mojang.math.Matrix4f source) {
        ModelMatrices local = LOCAL.get();
        source.store(local.buffer);
        return local.position.set(0, local.buffer);
    }

    public static org.joml.Matrix3f normal(com.mojang.math.Matrix3f source) {
        ModelMatrices local = LOCAL.get();
        source.store(local.buffer);
        return local.normal.set(0, local.buffer);
    }

    public static Quaternion quaternion(Quaternionfc q) { return new Quaternion(q.x(), q.y(), q.z(), q.w()); }

    public static void rotateAround(PoseStack pose, Quaternion rotation, float x, float y, float z) {
        pose.translate(x, y, z);
        pose.mulPose(rotation);
        pose.translate(-x, -y, -z);
    }

    public static void rotateAround(PoseStack pose, Quaternionfc rotation, float x, float y, float z) {
        rotateAround(pose, quaternion(rotation), x, y, z);
    }
}
