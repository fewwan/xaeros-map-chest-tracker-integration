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

package fewwan.xaerosmapchesttrackerintegration.util.bootstrap;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class XaerosMapChestTrackerIntegrationPreLaunchEntrypoint implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        MixinExtrasBootstrap.init();
    }
}
