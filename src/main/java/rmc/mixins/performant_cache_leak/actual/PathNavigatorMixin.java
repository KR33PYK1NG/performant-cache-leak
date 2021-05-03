package rmc.mixins.performant_cache_leak.actual;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.PathNavigator;
import rmc.mixins.performant_cache_leak.extend.PathNavigatorEx;

/**
 * Developed by RMC Team, 2021
 */
@Mixin(value = PathNavigator.class)
public abstract class PathNavigatorMixin
implements PathNavigatorEx {

    @Shadow @Final protected MobEntity entity;

    @Override
    public MobEntity rmc$getMobEntity() {
        return this.entity;
    }

}