package condolence.customstars.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import condolence.customstars.CustomStarsMod;
import condolence.customstars.config.CustomStarsConfig;
import condolence.customstars.noise.OctaveSimplexNoise;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = WorldRenderer.class, priority = 1)
public class MixinWorldRenderer {
    @Shadow private VertexBuffer starBuffer;
    @Shadow private VertexFormat skyFormat;
    private final Random noiseRandom = new Random(1L); // We want the seed to be constant here, so just use 1

    @Inject(method = "drawStars(Lnet/minecraft/client/renderer/BufferBuilder;)V", at = @At("HEAD"), cancellable = true)
    public void injectRenderStarsWithNoise(BufferBuilder buffer, CallbackInfo info) {
        renderStarsWithNoise(buffer);
        info.cancel();
    }

    @Inject(method = "renderSky(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V", at  = @At("HEAD"))
    private void reloadStars(CallbackInfo info) {
        // Trigger a re-draw of the stars on config reload
        // Stars are drawn only once, so this must be checked in a method which is invoked often
        if (CustomStarsConfig.updated) {
            CustomStarsMod.LOGGER.info("Config has been updated, redrawing stars...");

            BufferBuilder builder = Tessellator.getInstance().getBuilder();
            this.starBuffer = new VertexBuffer(this.skyFormat);

            ((MixinWorldRendererInvoker) this).rerenderStars(builder);

            builder.end();
            this.starBuffer.upload(builder);

            CustomStarsConfig.validateUpdate();
        }
    }

    @Redirect(method = "renderSky(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;color4f(FFFF)V", ordinal = 1))
    @SuppressWarnings("deprecation")
    private void modifyStarColor(float r, float g, float b, float a) {
        RenderSystem.color4f(
                r * CustomStarsConfig.red / 255F,
                g * CustomStarsConfig.green / 255F,
                b * CustomStarsConfig.blue / 255F,
                (float) (a * CustomStarsConfig.alpha)
        );
    }

    @Unique
    private void renderStarsWithNoise(BufferBuilder buffer) {
        OctaveSimplexNoise noiseGenerator = new OctaveSimplexNoise(noiseRandom, 3);

        int starCount = CustomStarsConfig.starCount;
        double baseSize = CustomStarsConfig.baseSize;
        double maxSizeMultiplier = CustomStarsConfig.maxSizeMultiplier;
        double noiseThreshold = CustomStarsConfig.noiseThreshold;

        double[] ipts = new double[starCount];
        double[] jpts = new double[starCount];
        double[] kpts = new double[starCount];

        // A percentage (defined by noisePercentage) of stars will use the custom noise generator,
        // the rest will use vanilla randomisation (world gen)
        int stars = 0;
        while (stars < (int) Math.floor(starCount * CustomStarsConfig.noisePercentage / 100D)) {
            double i = noiseRandom.nextFloat() * 2.0f - 1.0f;
            double j = noiseRandom.nextFloat() * 2.0f - 1.0f;
            double k = noiseRandom.nextFloat() * 2.0f - 1.0f;

            double weight = noiseGenerator.generate(i, j, k);

            if (weight + noiseRandom.nextDouble() * 0.2 > noiseThreshold) {
                ipts[stars] = i;
                jpts[stars] = j;
                kpts[stars] = k;

                stars++;
            }
        }

        while (stars < starCount) {
            ipts[stars] = noiseRandom.nextFloat() * 2.0f - 1.0f;
            jpts[stars] = noiseRandom.nextFloat() * 2.0f - 1.0f;
            kpts[stars] = noiseRandom.nextFloat() * 2.0f - 1.0f;

            stars++;
        }

        buffer.begin(7, DefaultVertexFormats.POSITION);

        for (int i = 0; i < starCount; ++i) {
            double double5 = ipts[i];
            double double7 = jpts[i];
            double double9 = kpts[i];

            double double11 = baseSize + noiseRandom.nextFloat() * maxSizeMultiplier;
            double double13 = double5 * double5 + double7 * double7 + double9 * double9;
            if (double13 < 1.0 && double13 > 0.01) {
                double13 = 1.0 / Math.sqrt(double13);
                double5 *= double13;
                double7 *= double13;
                double9 *= double13;
                double double15 = double5 * 100.0;
                double double17 = double7 * 100.0;
                double double19 = double9 * 100.0;
                double double21 = Math.atan2(double5, double9);
                double double23 = Math.sin(double21);
                double double25 = Math.cos(double21);
                double double27 = Math.atan2(Math.sqrt(double5 * double5 + double9 * double9), double7);
                double double29 = Math.sin(double27);
                double double31 = Math.cos(double27);
                double double33 = noiseRandom.nextDouble() * 3.141592653589793 * 2.0;
                double double35 = Math.sin(double33);
                double double37 = Math.cos(double33);
                for (int v = 0; v < 4; ++v) {
                    double double42 = ((v & 0x2) - 1) * double11;
                    double double44 = ((v + 1 & 0x2) - 1) * double11;
                    double double48 = double42 * double37 - double44 * double35;
                    double double52;
                    double52 = double44 * double37 + double42 * double35;
                    double double54 = double48 * double29 + 0.0 * double31;
                    double double56 = 0.0 * double29 - double48 * double31;
                    double double58 = double56 * double23 - double52 * double25;
                    double double62 = double52 * double23 + double56 * double25;
                    buffer.vertex(double15 + double58, double17 + double54, double19 + double62)
                            .endVertex();
                }
            }
        }
    }
}
