/*
 * This file is part of the Xaero's Map Chest Tracker Integration project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fewwan and contributors
 *
 * Xaero's Map Chest Tracker Integration is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Xaero's Map Chest Tracker Integration is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Xaero's Map Chest Tracker Integration.  If not, see <https://www.gnu.org/licenses/>.
 */

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

import static fewwan.xaerosmapchesttrackerintegration.XaerosMapChestTrackerIntegration.MOD_ID;


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
