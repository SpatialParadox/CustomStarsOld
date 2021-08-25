package condolence.customstars;

import condolence.customstars.config.CustomStarsConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CustomStarsMod.MOD_ID)
public class CustomStarsMod {
    public static final String MOD_ID = "customstars";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public CustomStarsMod() {
        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CustomStarsConfig.CONFIG_SPEC);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(CustomStarsConfig::onLoad);
        }
    }
}
