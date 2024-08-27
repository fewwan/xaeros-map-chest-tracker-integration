package fewwan.xaerosmapchesttrackerintegration;

import fewwan.xaerosmapchesttrackerintegration.util.ModCheckUtils;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class XaerosMapChestTrackerIntegrationClient implements ClientModInitializer {
    public static final String MOD_ID = "xaeros-map-chest-tracker-integration";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        if (!ModCheckUtils.isXaeroMinimapModLoaded()) {
            LOGGER.error("Xaero's Minimap mod not found. Please install it to use this mod.");
        }
    }
}