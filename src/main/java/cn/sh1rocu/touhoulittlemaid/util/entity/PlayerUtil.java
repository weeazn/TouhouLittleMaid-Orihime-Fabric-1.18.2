package cn.sh1rocu.touhoulittlemaid.util.entity;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PlayerUtil {
    public static boolean canReach(Player player, Entity entity, double padding) {
        return isCloseEnough(player, entity, getEntityReach(player) + padding);
    }

    public static boolean isCloseEnough(Player player, Entity entity, double dist) {
        Vec3 eye = player.getEyePosition();
        AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius());
        return eye.distanceToSqr(net.minecraft.util.Mth.clamp(eye.x, aabb.minX, aabb.maxX),
                net.minecraft.util.Mth.clamp(eye.y, aabb.minY, aabb.maxY),
                net.minecraft.util.Mth.clamp(eye.z, aabb.minZ, aabb.maxZ)) < dist * dist;
    }

    public static double getEntityReach(Player player) {
        double range = player.getAttributeValue(ReachEntityAttributes.REACH) + 3;
        return range == (double) 0.0F ? (double) 0.0F : range + (double) (player.isCreative() ? 3 : 0);
    }
}
