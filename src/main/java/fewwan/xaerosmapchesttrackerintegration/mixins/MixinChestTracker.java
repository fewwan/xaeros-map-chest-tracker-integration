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

package fewwan.xaerosmapchesttrackerintegration.mixins;

import fewwan.xaerosmapchesttrackerintegration.util.CountUtils;
import fewwan.xaerosmapchesttrackerintegration.util.mixin.testers.XaeroMinimapTester;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jackf.chesttracker.memory.Memory;
import red.jackf.chesttracker.memory.MemoryDatabase;
import xaero.common.XaeroMinimapSession;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointSet;
import xaero.common.minimap.waypoints.WaypointsManager;

import java.util.List;

@Restriction(
        require = @Condition(type = Condition.Type.TESTER, tester = XaeroMinimapTester.class)
)
@Mixin(red.jackf.chesttracker.ChestTracker.class)
public class MixinChestTracker {
    @Inject(method = "searchForItem", at = @At("HEAD"))
    private static void createWaypoints(ItemStack stack, CallbackInfo ci) {
        MemoryDatabase database = MemoryDatabase.getCurrent();
        if (database == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        if (world == null) return;
        Item searchItem = stack.getItem();

        XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        if (minimapSession == null) return;
        WaypointsManager waypointsManager = minimapSession.getWaypointsManager();
        WaypointSet waypointSet = waypointsManager.getWaypoints();
        if (waypointSet == null) return;
        List<Waypoint> waypoints = waypointSet.getList();

        List<Memory> memories = database.findItems(stack, world.getRegistryKey().getValue());
        for (Memory memory : memories) {
            List<ItemStack> items = memory.getItems();
            int totalItemCount = CountUtils.countItemsOf(items, searchItem);

            BlockPos position = memory.getPosition();
            int x = position.getX();
            int y = position.getY();
            int z = position.getZ();

            String itemName = Text.translatable(searchItem.getTranslationKey()).getString();
            String waypointName = totalItemCount + " " + itemName + " [CT]";
//            String waypointLabel = String.valueOf(totalItemCount);
            String waypointLabel = totalItemCount <= 99 ? String.valueOf(totalItemCount) : "99";
            int waypointColor = 0;

            // XaerosMapChestTrackerIntegration.LOGGER.info("Debug message: Found " + totalItemCount + " items of " + itemName + " at " + x + ", " + y + ", " + z);
            waypoints.add(new Waypoint(x, y, z, waypointName, waypointLabel, waypointColor, 0, true));
        }
    }
}
