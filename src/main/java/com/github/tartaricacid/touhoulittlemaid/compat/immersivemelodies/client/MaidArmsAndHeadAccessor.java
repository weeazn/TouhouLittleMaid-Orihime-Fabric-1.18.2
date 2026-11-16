package com.github.tartaricacid.touhoulittlemaid.compat.immersivemelodies.client;

import com.github.tartaricacid.simplebedrockmodel.client.bedrock.model.BedrockPart;
import immersive_melodies.client.animation.BipedEntityModelAnimator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Mob;

/** Apply instrument animations from the 1.18 humanoid API to the maid's Bedrock bones. */
public final class MaidArmsAndHeadAccessor {
    private final HumanoidModel<Mob> model = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER));

    public void animate(Mob maid, BedrockPart head, BedrockPart hat, BedrockPart left, BedrockPart right) {
        if (BipedEntityModelAnimator.getInstrument(maid) == null) {
            return;
        }
        readRotation(model.head, head);
        readRotation(model.hat, hat);
        readRotation(model.leftArm, left);
        readRotation(model.rightArm, right);
        BipedEntityModelAnimator.setAngles(model, maid);
        writeRotation(head, model.head);
        writeRotation(hat, model.hat);
        writeRotation(left, model.leftArm);
        writeRotation(right, model.rightArm);
    }

    private static void readRotation(ModelPart target, BedrockPart source) {
        target.xRot = source == null ? 0 : source.xRot;
        target.yRot = source == null ? 0 : source.yRot;
        target.zRot = source == null ? 0 : source.zRot;
    }

    private static void writeRotation(BedrockPart target, ModelPart source) {
        if (target != null) {
            target.xRot = source.xRot;
            target.yRot = source.yRot;
            target.zRot = source.zRot;
        }
    }
}
