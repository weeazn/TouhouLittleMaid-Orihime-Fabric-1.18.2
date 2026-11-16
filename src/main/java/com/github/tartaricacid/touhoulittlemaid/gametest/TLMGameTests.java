package com.github.tartaricacid.touhoulittlemaid.gametest;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;


public class TLMGameTests {
    @GameTest(template = TouhouLittleMaid.MOD_ID + ":game_test")
    public static void tlmMaidSpawn(GameTestHelper helper) {
        Player player = helper.makeMockPlayer();
        ItemStack smartSlab = InitItems.SMART_SLAB_INIT.getDefaultInstance();
        player.setItemInHand(InteractionHand.MAIN_HAND, smartSlab);

        // 在中心位置放置女仆
        BlockPos groundPos = new BlockPos(7, 1, 7);
        useItemOn(helper, player, smartSlab, groundPos, Direction.UP);

        // 女仆生成在 groundPos 上方，延迟 2 tick 后验证
        helper.runAfterDelay(2, () -> {
            List<EntityMaid> entities = helper.getLevel().getEntitiesOfClass(EntityMaid.class,
                    new net.minecraft.world.phys.AABB(helper.absolutePos(groundPos.above())).inflate(1));
            if (entities.size() != 1) {
                helper.fail("Expected one maid after using the smart slab", groundPos);
                return;
            }
            EntityMaid maid = entities.get(0);
            maid.getSchedulePos().setHomeModeEnable(maid, maid.blockPosition());
            maid.setHomeModeEnable(true);
            helper.succeed();
        });
    }

    private static void useItemOn(GameTestHelper helper, Player player, ItemStack stack, BlockPos relativePos, Direction face) {
        BlockPos absolutePos = helper.absolutePos(relativePos);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(absolutePos), face, absolutePos, false);
        UseOnContext context = new UseOnContext(player, InteractionHand.MAIN_HAND, hit);
        stack.useOn(context);
    }
}