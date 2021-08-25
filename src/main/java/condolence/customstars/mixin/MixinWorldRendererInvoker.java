package condolence.customstars.mixin;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WorldRenderer.class)
public interface MixinWorldRendererInvoker {
    @Invoker("drawStars")
    void rerenderStars(BufferBuilder builder);
}
