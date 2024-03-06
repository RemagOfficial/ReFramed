package fr.adrien1106.reframed.mixin.particles;

import com.llamalad7.mixinextras.sugar.Local;
import fr.adrien1106.reframed.block.ReFramedBlock;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Entity.class)
public abstract class MixinEntity {
	@ModifyArg(
		method = "spawnSprintingParticles",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/particle/BlockStateParticleEffect;<init>(Lnet/minecraft/particle/ParticleType;Lnet/minecraft/block/BlockState;)V")
	)
	private BlockState modifyParticleState(BlockState state, @Local(ordinal = 0) BlockPos landing_pos) {
		World world = ((Entity) (Object) this).getWorld();
		
		if(world.getBlockEntity(landing_pos) instanceof ThemeableBlockEntity themeable
			&& state.getBlock() instanceof ReFramedBlock block) {
				BlockState theme = themeable.getTheme(block.getTopThemeIndex(state));
				if(!theme.isAir()) return theme;
		}
		
		return state;
	}
}
