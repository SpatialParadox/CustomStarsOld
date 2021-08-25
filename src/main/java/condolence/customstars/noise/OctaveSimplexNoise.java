package condolence.customstars.noise;

import net.minecraft.world.gen.SimplexNoiseGenerator;

import java.util.Random;

public class OctaveSimplexNoise {
    private final SimplexNoiseGenerator[] generators;
    private final int octaves;

    public OctaveSimplexNoise(Random random, int octaves) {
        this.octaves = octaves;
        this.generators = new SimplexNoiseGenerator[octaves];

        for (int i = 0; i < octaves; ++i) {
            this.generators[i] = new SimplexNoiseGenerator(random);
        }
    }

    public double generate(double x, double y, double z) {
        double total = 0.0D;
        double frequency = 1.0D;
        double amplitude = 1.0D;

        double persistence = 0.5D;
        double lacunarity = 2.0D;

        for (int i = 0; i < this.octaves; ++i) {
            total += this.generators[i].getValue(x * frequency, y * frequency, z * frequency)
                    * amplitude;

            amplitude *= persistence;
            frequency *= lacunarity;
        }

        return total;

    }
}