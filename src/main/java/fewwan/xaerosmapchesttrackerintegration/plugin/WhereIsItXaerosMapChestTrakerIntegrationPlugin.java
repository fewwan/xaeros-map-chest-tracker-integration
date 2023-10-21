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

package fewwan.xaerosmapchesttrackerintegration.plugin;

import fewwan.xaerosmapchesttrackerintegration.util.CountUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import red.jackf.chesttracker.memory.Memory;
import red.jackf.chesttracker.memory.MemoryBank;
import red.jackf.chesttracker.provider.ProviderHandler;
import red.jackf.whereisit.api.SearchResult;
import red.jackf.whereisit.client.api.WhereIsItClientPlugin;
import red.jackf.whereisit.client.api.events.OnResult;
import red.jackf.whereisit.client.api.events.OnResultsCleared;
import xaero.common.XaeroMinimapSession;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointSet;
import xaero.common.minimap.waypoints.WaypointsManager;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public class WhereIsItXaerosMapChestTrakerIntegrationPlugin implements WhereIsItClientPlugin {
    private static boolean running = false;
    private static final String suffix = " [CT]";
    private static final int colorId = 0;

    @Override
    public void load() {
        OnResult.EVENT.register(WhereIsItXaerosMapChestTrakerIntegrationPlugin::onResults);
        OnResultsCleared.EVENT.register(WhereIsItXaerosMapChestTrakerIntegrationPlugin::OnResultsCleared);
    }

    private static void onResults(Collection<SearchResult> results) {
        running = true;
        if (ProviderHandler.INSTANCE == null || MemoryBank.INSTANCE == null) return;

        XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        if (minimapSession == null) return;
        WaypointsManager waypointsManager = minimapSession.getWaypointsManager();
        WaypointSet waypointSet = waypointsManager.getWaypoints();
        if (waypointSet == null) return;
        List<Waypoint> waypoints = waypointSet.getList();

        Identifier key = ProviderHandler.getCurrentKey();
        Map<BlockPos, Memory> memoryKeys = MemoryBank.INSTANCE.getMemories(key);
        if (memoryKeys == null) return;

        for (SearchResult result : results) {
            BlockPos pos = result.pos();
            ItemStack searchStack = result.item();
            Item searchItem = searchStack.getItem();

            Memory memory = memoryKeys.get(pos);
            if (memory == null) return;

            int totalItemCount = CountUtils.countItemsOf(memory.items(), searchItem);

            String itemName = Text.translatable(searchStack.getTranslationKey()).getString();
            String waypointName = totalItemCount + " " + itemName + suffix;
            String waypointLabel = String.valueOf(totalItemCount);
//            String waypointLabel = totalItemCount <= 99 ? String.valueOf(totalItemCount) : "99";

            waypoints.add(new Waypoint(pos.getX(), pos.getY(), pos.getZ(),
                    waypointName, waypointLabel, colorId, 0, true));

        }
    }

    private static void OnResultsCleared() {
        if (!running) return;
        running = false;

        XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        if (minimapSession == null) return;
        final WaypointsManager waypointsManager = minimapSession.getWaypointsManager();
        WaypointSet waypointSet = waypointsManager.getWaypoints();
        if (waypointSet == null) return;
        final List<Waypoint> waypoints = waypointSet.getList();

        waypoints.removeIf(waypoint -> waypoint.isTemporary() && waypoint.getName().endsWith(suffix));
    }
}
