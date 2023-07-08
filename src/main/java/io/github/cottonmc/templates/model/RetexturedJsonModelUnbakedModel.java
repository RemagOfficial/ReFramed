package io.github.cottonmc.templates.model;

import io.github.cottonmc.templates.Templates;
import io.github.cottonmc.templates.TemplatesClient;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class RetexturedJsonModelUnbakedModel implements UnbakedModel {
	public RetexturedJsonModelUnbakedModel(Identifier parent) {
		this(parent, Blocks.AIR.getDefaultState());
	}
	
	public RetexturedJsonModelUnbakedModel(Identifier parent, BlockState itemModelState) {
		this.parent = parent;
		this.itemModelState = itemModelState;
	}
	
	protected final Identifier parent;
	protected final BlockState itemModelState;
	
	@Override
	public Collection<Identifier> getModelDependencies() {
		return Collections.singletonList(parent);
	}
	
	@Override
	public void setParents(Function<Identifier, UnbakedModel> function) {
		function.apply(parent).setParents(function);
	}
	
	@Nullable
	@Override
	public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> spriteLookup, ModelBakeSettings modelBakeSettings, Identifier identifier) {
		Direction[] DIRECTIONS = RetexturingBakedModel.DIRECTIONS;
		
		Sprite[] specialSprites = new Sprite[DIRECTIONS.length];
		for(int i = 0; i < DIRECTIONS.length; i++) {
			SpriteIdentifier id = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, Templates.id("templates_special/" + DIRECTIONS[i].getName()));
			specialSprites[i] = Objects.requireNonNull(spriteLookup.apply(id), () -> "Couldn't find sprite " + id + " !");
		}
		
		ConcurrentMap<BlockState, Mesh> jsonToMesh = new ConcurrentHashMap<>();
		
		return new RetexturingBakedModel(
			baker.bake(parent, modelBakeSettings),
			TemplatesClient.provider.getOrCreateTemplateApperanceManager(spriteLookup),
			modelBakeSettings,
			itemModelState
		) {
			@Override
			protected Mesh getBaseMesh(BlockState state) {
				//Convert models to retexturable Meshes lazily, the first time we encounter each blockstate
				return jsonToMesh.computeIfAbsent(state, this::convertModel);
			}
			
			private Mesh convertModel(BlockState state) {
				Renderer r = TemplatesClient.getFabricRenderer();
				MeshBuilder builder = r.meshBuilder();
				QuadEmitter emitter = builder.getEmitter();
				RenderMaterial mat = tam.getCachedMaterial(state);
				
				Random rand = Random.create(42);
				
				for(Direction cullFace : DIRECTIONS_AND_NULL) {
					for(BakedQuad quad : wrapped.getQuads(state, cullFace, rand)) {
						emitter.fromVanilla(quad, mat, cullFace);
						
						QuadUvBounds bounds = QuadUvBounds.read(emitter);
						for(int i = 0; i < specialSprites.length; i++) {
							if(bounds.displaysSprite(specialSprites[i])) {
								bounds.normalizeUv(emitter, specialSprites[i]);
								emitter.tag(i + 1);
								break;
							}
						}
						
						emitter.emit();
					}
				}
				
				return builder.build();
			}
		};
	}
}
