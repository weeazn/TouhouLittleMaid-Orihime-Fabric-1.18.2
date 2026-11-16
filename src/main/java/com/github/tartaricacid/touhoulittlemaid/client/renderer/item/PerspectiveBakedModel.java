package com.github.tartaricacid.touhoulittlemaid.client.renderer.item;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.BakedModel;

public class PerspectiveBakedModel extends ForwardingBakedModel {
    private final BakedModel inHand;

    public PerspectiveBakedModel(BakedModel flat, BakedModel inHand) {
        this.wrapped = flat;
        this.inHand = inHand;
    }

    public BakedModel forTransform(TransformType type) {
        return type == TransformType.GUI || type == TransformType.FIXED ? wrapped : inHand;
    }
}
