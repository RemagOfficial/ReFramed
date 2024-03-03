package fr.adrien1106.reframed.mixin.compat;

import fr.adrien1106.reframed.block.ReFramedBlock;
import fr.adrien1106.reframed.util.IBlockRenderInfoMixin;
import fr.adrien1106.reframed.util.ThemeableBlockEntity;
import link.infra.indium.renderer.render.BlockRenderInfo;
import link.infra.indium.renderer.render.TerrainBlockRenderInfo;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TerrainBlockRenderInfo.class)
public abstract class IndiumTerrainBlockRenderInfoMixin extends BlockRenderInfo implements IBlockRenderInfoMixin {

    @Unique private int theme_index = 1;

    @Redirect(
        method = "shouldDrawFaceInner",
        at = @At(
            value = "INVOKE",
            target = "Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/BlockOcclusionCache;shouldDrawSide(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z"
        )
    )
    private boolean shouldDrawCamoSide(BlockOcclusionCache instance, BlockState state, BlockView view, BlockPos pos, Direction face) {
        BlockPos other_pos = pos.offset(face);
        if (!(view.getBlockEntity(pos) instanceof ThemeableBlockEntity
            || view.getBlockEntity(other_pos) instanceof ThemeableBlockEntity))
                return instance.shouldDrawSide(state, view, pos, face);
        return ReFramedBlock.shouldDrawSide(state, view, pos, face, other_pos, theme_index);
    }

    @Override
    public void prepareForBlock(BlockState blockState, BlockPos blockPos, long seed, boolean modelAo, int theme_index) {
        this.theme_index = theme_index;
        prepareForBlock(blockState, blockPos, seed, modelAo);
    }
}
