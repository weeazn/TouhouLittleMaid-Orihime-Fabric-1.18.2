package com.github.tartaricacid.touhoulittlemaid.compat.gun.swarfare.client;

import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import com.mojang.math.Vector3f;
import net.minecraft.core.Registry;
import com.atsuishio.superbwarfare.item.DispenserLaunchable;
import com.atsuishio.superbwarfare.item.gun.GunItem;
import com.github.tartaricacid.touhoulittlemaid.api.backpack.IMaidBackpack;
import com.github.tartaricacid.touhoulittlemaid.api.entity.IMaid;
import com.github.tartaricacid.touhoulittlemaid.compat.gun.swarfare.SWarfareCompat;
import com.github.tartaricacid.touhoulittlemaid.entity.backpack.BackpackManager;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated.ILocationModel;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import static com.github.tartaricacid.touhoulittlemaid.compat.gun.swarfare.SWarfareCompat.PISTOL;

@Environment(EnvType.CLIENT)
public class GunMaidRender {
    public static void addItemTranslate(PoseStack matrixStack, ItemStack itemStack, boolean isLeft) {
        if (!isLeft && itemStack.getItem() instanceof GunItem gun) {
            matrixStack.translate(0, -0.0625, 0);
            ResourceLocation id = Registry.ITEM.getKey(gun);
            if (SWarfareCompat.MINIGUN_ID.equals(id)) {
                matrixStack.mulPose(Vector3f.ZP.rotationDegrees(20));
                matrixStack.mulPose(Vector3f.XP.rotationDegrees(25));
            } else if (SWarfareCompat.M_2_HB_ID.equals(id)) {
                matrixStack.mulPose(Vector3f.ZP.rotationDegrees(10));
                matrixStack.mulPose(Vector3f.XP.rotationDegrees(10));
            }
        }
    }

    public static void renderBackGun(PoseStack matrixStack, MultiBufferSource bufferIn, int packedLightIn, ItemStack stack, IMaid maid) {
        Item item = stack.getItem();
        if (!(item instanceof GunItem) && !(item instanceof DispenserLaunchable)) {
            return;
        }
        matrixStack.pushPose();
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
        matrixStack.translate(0, 0.5, -0.25);
        if (maid instanceof EntityMaid entityMaid && entityMaid.getConfigManager().isShowBackpack()) {
            maid.getMaidBackpackType().offsetBackpackItem(matrixStack);
        } else {
            BackpackManager.getEmptyBackpack().offsetBackpackItem(matrixStack);
        }
        {
            matrixStack.pushPose();
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-35));
            matrixStack.scale(0.6f, 0.6f, 0.6f);
            Mob mob = maid.asEntity();
            Minecraft.getInstance().getItemRenderer().renderStatic(mob, stack, TransformType.FIXED, false, matrixStack, bufferIn, mob.getLevel(), packedLightIn, OverlayTexture.NO_OVERLAY, mob.getId());
            matrixStack.popPose();
        }
        matrixStack.popPose();
    }

    public static void renderBackGun(ItemStack heldItem, ILocationModel geoModel, IMaid maid, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Item item = heldItem.getItem();
        if (!(item instanceof GunItem) && !(item instanceof DispenserLaunchable)) {
            return;
        }
        Mob entity = maid.asEntity();
        IMaidBackpack maidBackpackType = maid.getMaidBackpackType();
        // 如果女仆穿戴了背包，且配置文件允许显示背包
        // 直接调用背包渲染
        if (entity instanceof EntityMaid entityMaid && entityMaid.getConfigManager().isShowBackpack() && maidBackpackType != BackpackManager.getEmptyBackpack()) {
            if (!geoModel.backpackBones().isEmpty()) {
                RenderUtils.prepMatrixForLocator(poseStack, geoModel.backpackBones());
            }
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(180));
            poseStack.translate(0, -1, 0.25);
            renderBackGun(poseStack, buffer, packedLight, heldItem, maid);
            return;
        }

        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        boolean showInWaist = heldItem.is(PISTOL) || item instanceof DispenserLaunchable;
        if (showInWaist && !geoModel.tacPistolBones().isEmpty()) {
            RenderUtils.prepMatrixForLocator(poseStack, geoModel.tacPistolBones());

            poseStack.translate(0, -0.125, 0);
            poseStack.scale(0.65f, 0.65f, 0.65f);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(90));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(-90.0F));
            renderer.renderStatic(null, heldItem, TransformType.FIXED, false, poseStack, buffer, entity.getLevel(), packedLight, OverlayTexture.NO_OVERLAY, entity.getId());
        }
        if (!showInWaist && !geoModel.tacRifleBones().isEmpty()) {
            RenderUtils.prepMatrixForLocator(poseStack, geoModel.tacRifleBones());

            poseStack.scale(0.65f, 0.65f, 0.65f);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(-180.0F));
            renderer.renderStatic(null, heldItem, TransformType.FIXED, false, poseStack, buffer, entity.getLevel(), packedLight, OverlayTexture.NO_OVERLAY, entity.getId());
        }
    }
}