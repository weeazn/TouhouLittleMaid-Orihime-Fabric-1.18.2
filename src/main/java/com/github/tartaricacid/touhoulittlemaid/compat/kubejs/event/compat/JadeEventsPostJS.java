package com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.compat;

import com.github.tartaricacid.touhoulittlemaid.api.event.AddJadeInfoEvent;
import com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.MaidEventsJS;
import com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.common.AddJadeInfoEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;

public class JadeEventsPostJS {
    public void addJadeInfo(AddJadeInfoEvent event) {
        ScriptType scriptType = event.getMaid().level.isClientSide ? ScriptType.CLIENT : ScriptType.SERVER;
        new AddJadeInfoEventJS(event).post(scriptType, "maid.addJadeInfo");
    }
}
