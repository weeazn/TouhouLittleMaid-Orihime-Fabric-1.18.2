package cn.sh1rocu.touhoulittlemaid.mixin.accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(Screen.class)
public interface ScreenAccessor {
    @Accessor("renderables")
    List<Widget> tlm$getRenderables();
}
