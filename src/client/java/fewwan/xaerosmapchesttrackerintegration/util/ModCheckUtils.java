package fewwan.xaerosmapchesttrackerintegration.util;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import net.fabricmc.loader.impl.util.version.VersionPredicateParser;

import java.util.Map;
import java.util.Optional;

import static fewwan.xaerosmapchesttrackerintegration.XaerosMapChestTrackerIntegrationClient.MOD_ID;


public class ModCheckUtils {
    public static boolean isXaeroMinimapModLoaded() {
        FabricLoader loader = FabricLoader.getInstance();
        ModContainer mod = loader.getModContainer(MOD_ID).orElseThrow(NullPointerException::new);
        CustomValue.CvObject validXaerosMinimap = mod.getMetadata().getCustomValue(MOD_ID).getAsObject().get("validXaerosMinimap").getAsObject();

        for (Map.Entry<String, CustomValue> entry : validXaerosMinimap) {
            CustomValue value = entry.getValue();
            String modId = entry.getKey();

            // means that there is not a compatible version yet
            if (value.getType() == CustomValue.CvType.NULL) continue;

            String versionConstraintString = value.getAsString();

            // means that there is not a compatible version yet
            if (versionConstraintString.equals("none")) continue;

            VersionPredicate versionConstraint;
            try {
                versionConstraint = VersionPredicateParser.parse(versionConstraintString);
            } catch (VersionParsingException e) {
                throw new RuntimeException(e);
            }

            Optional<ModContainer> minimapMod = loader.getModContainer(modId);
            if (minimapMod.isPresent()) {
                ModContainer minimapModContainer = minimapMod.get();
                Version minimapVersion = minimapModContainer.getMetadata().getVersion();
                return versionConstraint.test(minimapVersion);
            }
        }

        return false;
    }
}
