package rmc.mixins.performant_cache_leak.actual;

import java.lang.reflect.Field;
import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import rmc.mixins.performant_cache_leak.extend.PathNavigatorEx;

/**
 * Developed by RMC Team, 2021
 */
@Mixin(value = ServerWorld.class)
public abstract class ServerWorldMixin {

    private static final Field rmc$CURRENT_TICK;
    private static final Field rmc$CHUNK_CACHE;

    static {
        Field currentTick = null;
        Field chunkCache = null;
        try {
            currentTick = MinecraftServer.class.getField("currentTick");
            chunkCache = LivingEntity.class.getDeclaredField("chunkCache");
            chunkCache.setAccessible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        rmc$CURRENT_TICK = currentTick;
        rmc$CHUNK_CACHE = chunkCache;
    }

    @Shadow @Final public Int2ObjectMap<Entity> entitiesById;
    @Shadow @Final private Set<PathNavigator> navigations;

    @Inject(method = "Lnet/minecraft/world/server/ServerWorld;tick(Ljava/util/function/BooleanSupplier;)V",
            at = @At(value = "HEAD"))
    private void cleanupChunkCaches(CallbackInfo mixin) {
        try {
            if (rmc$CURRENT_TICK.getInt(null) % 1200 == 0) {
                for (Entity entity : this.entitiesById.values()) {
                    if (entity instanceof LivingEntity) {
                        rmc$CHUNK_CACHE.set(entity, null);
                    }
                }
                for (PathNavigator nav : this.navigations) {
                    rmc$CHUNK_CACHE.set(((PathNavigatorEx) nav).rmc$getMobEntity(), null);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}