package io.github.cottonmc.templates.model;

import io.github.cottonmc.templates.TemplatesClient;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.math.Direction;

import java.util.function.Function;

public class SlopeBaseMesh {
	/**
	 * @see RetexturingBakedModel for why these values were chosen
	 */
	public static final int TAG_SLOPE = Direction.UP.ordinal() + 1;
	public static final int TAG_LEFT = Direction.EAST.ordinal() + 1;
	public static final int TAG_RIGHT = Direction.WEST.ordinal() + 1;
	public static final int TAG_BACK = Direction.SOUTH.ordinal() + 1;
	public static final int TAG_BOTTOM = Direction.DOWN.ordinal() + 1;
	
	public static Mesh make() {
		Renderer renderer = TemplatesClient.getFabricRenderer();
		
		MeshBuilder builder = renderer.meshBuilder();
		QuadEmitter qu = builder.getEmitter();
		
		qu.tag(TAG_SLOPE)
			.pos(0, 0f, 0f, 0f).pos(1, 0f, 1f, 1f).pos(2, 1f, 1f, 1f).pos(3, 1f, 0f, 0f)
			.color(-1, -1, -1, -1)
			.uv(0, 0f, 0f).uv(1, 0f, 1f).uv(2, 1f, 1f).uv(3, 1f, 0f)
			.emit()
			.tag(TAG_LEFT)
			.pos(0, 1f, 0f, 0f).pos(1, 1f, 0.5f, 0.5f).pos(2, 1f, 1f, 1f).pos(3, 1f, 0f, 1f)
			.color(-1, -1, -1, -1)
			.uv(0, 1f, 1f).uv(1, 0.5f, 0.5f).uv(2, 0f, 0f).uv(3, 0f, 1f)
			.cullFace(Direction.EAST)
			.emit()
			.tag(TAG_RIGHT)
			.pos(0, 0f, 0.5f, 0.5f).pos(1, 0, 0f, 0f).pos(2, 0f, 0f, 1f).pos(3, 0f, 1f, 1f)
			.color(-1, -1, -1, -1)
			.uv(0, 0.5f, 0.5f).uv(1, 0f, 1f).uv(2, 1f, 1f).uv(3, 1f, 0f)
			.cullFace(Direction.WEST)
			.emit()
			.tag(TAG_BACK)
			.square(Direction.SOUTH, 0, 0, 1, 1, 0) //sets pos & cullFace
			.color(-1, -1, -1, -1)
			.uvUnitSquare()
			.emit()
			.tag(TAG_BOTTOM)
			.square(Direction.DOWN, 0, 0, 1, 1, 0) //sets pos & cullFace
			.color(-1, -1, -1, -1)
			.uvUnitSquare()
			.emit();
		return builder.build();
	}
}
