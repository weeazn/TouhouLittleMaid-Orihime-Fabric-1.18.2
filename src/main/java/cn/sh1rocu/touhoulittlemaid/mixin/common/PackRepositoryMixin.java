package cn.sh1rocu.touhoulittlemaid.mixin.common;

import cn.sh1rocu.touhoulittlemaid.api.mixin.PackRepositoryExtension;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.LinkedHashSet;
import java.util.Set;

@Mixin(PackRepository.class)
public class PackRepositoryMixin implements PackRepositoryExtension {
    @Mutable
    @Shadow
    @Final
    private Set<RepositorySource> sources;

    @org.spongepowered.asm.mixin.injection.Inject(
            method = "<init>(Lnet/minecraft/server/packs/PackType;[Lnet/minecraft/server/packs/repository/RepositorySource;)V",
            at = @org.spongepowered.asm.mixin.injection.At("RETURN"))
    private void tlm$serverDataSources(net.minecraft.server.packs.PackType type, RepositorySource[] sources,
            org.spongepowered.asm.mixin.injection.callback.CallbackInfo callback) {
        if (type == net.minecraft.server.packs.PackType.SERVER_DATA) {
            var event = new cn.sh1rocu.touhoulittlemaid.api.event.AddPackFindersEvent(type, this::tlm$addPackFinder, false);
            cn.sh1rocu.touhoulittlemaid.api.event.AddPackFindersEvent.CALLBACK.invoker().onAddPackFinders(event);
        }
    }

    @Override
    public synchronized void tlm$addPackFinder(RepositorySource packFinder) {
        if (!(this.sources instanceof LinkedHashSet)) {
            this.sources = new LinkedHashSet<>(this.sources);
        }
        this.sources.add(packFinder);
    }
}