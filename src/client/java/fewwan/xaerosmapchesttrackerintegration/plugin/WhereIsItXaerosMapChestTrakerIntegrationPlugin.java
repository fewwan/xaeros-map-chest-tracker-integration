package fewwan.xaerosmapchesttrackerintegration.plugin;

import fewwan.xaerosmapchesttrackerintegration.util.CountUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import red.jackf.chesttracker.api.memory.Memory;
import red.jackf.chesttracker.api.memory.MemoryBank;
import red.jackf.chesttracker.api.providers.ProviderUtils;
import red.jackf.chesttracker.impl.memory.MemoryBankAccessImpl;
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
import java.util.Optional;

public class WhereIsItXaerosMapChestTrakerIntegrationPlugin implements WhereIsItClientPlugin {
    private static final String SUFFIX = " [CT]";
    private static final int COLOR_ID = 0;
    private static boolean running = false;

    private static void onResults(Collection<SearchResult> results) {
        running = true;

        XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        if (minimapSession == null) return;

        WaypointsManager waypointsManager = minimapSession.getWaypointsManager();
        WaypointSet waypointSet = waypointsManager.getWaypoints();
        if (waypointSet == null) return;

        List<Waypoint> waypoints = waypointSet.getList();

        Optional<Identifier> key = ProviderUtils.getPlayersCurrentKey();
        Optional<MemoryBank> optionalMemoryBank = MemoryBankAccessImpl.INSTANCE.getLoaded();
        if (optionalMemoryBank.isEmpty()) return;

        MemoryBank memoryBank = optionalMemoryBank.get();

        for (SearchResult result : results) {
            BlockPos pos = result.pos();
            ItemStack searchStack = result.item();
            Item searchItem = searchStack.getItem();

            Optional<Memory> optionalMemory = memoryBank.getMemory(key, pos);
            if (optionalMemory.isEmpty()) continue;

            Memory memory = optionalMemory.get();
            int totalItemCount = CountUtils.countItemsOf(memory.items(), searchItem);

            String itemName = Text.translatable(searchStack.getTranslationKey()).getString();
            String waypointName = totalItemCount + " " + itemName + SUFFIX;
            String waypointLabel = totalItemCount <= 99 ? String.valueOf(totalItemCount) : "99";

            waypoints.add(new Waypoint(pos.getX(), pos.getY(), pos.getZ(),
                    waypointName, waypointLabel, COLOR_ID, 0, true));
        }
    }

    private static void onResultsCleared() {
        if (!running) return;
        running = false;

        XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        if (minimapSession == null) return;

        WaypointsManager waypointsManager = minimapSession.getWaypointsManager();
        WaypointSet waypointSet = waypointsManager.getWaypoints();
        if (waypointSet == null) return;

        List<Waypoint> waypoints = waypointSet.getList();
        waypoints.removeIf(waypoint -> waypoint.isTemporary() && waypoint.getName().endsWith(SUFFIX));
    }

    @Override
    public void load() {
        OnResult.EVENT.register(WhereIsItXaerosMapChestTrakerIntegrationPlugin::onResults);
        OnResultsCleared.EVENT.register(WhereIsItXaerosMapChestTrakerIntegrationPlugin::onResultsCleared);
    }
}
