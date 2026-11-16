package cn.sh1rocu.touhoulittlemaid.compat.modmenu;

import com.github.tartaricacid.touhoulittlemaid.compat.cloth.MenuIntegration;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (FabricLoader.getInstance().isModLoaded("cloth-config"))
            return parent -> MenuIntegration.getConfigBuilder().setParentScreen(parent).build();
        return null;
    }
}