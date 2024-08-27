package fewwan.xaerosmapchesttrackerintegration.plugin;

import fewwan.xaerosmapchesttrackerintegration.util.CountUtils;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import red.jackf.chesttracker.api.memory.Memory;
import red.jackf.chesttracker.api.memory.MemoryBank;
import red.jackf.chesttracker.api.providers.ProviderUtils;
import red.jackf.chesttracker.impl.memory.MemoryBankAccessImpl;
import red.jackf.whereisit.api.SearchRequest;
import red.jackf.whereisit.api.SearchResult;
import red.jackf.whereisit.client.api.WhereIsItClientPlugin;
import red.jackf.whereisit.client.api.events.OnResult;
import red.jackf.whereisit.client.api.events.OnResultsCleared;
import red.jackf.whereisit.client.api.events.SearchInvoker;
import xaero.common.XaeroMinimapSession;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointSet;
import xaero.common.minimap.waypoints.WaypointsManager;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class WhereIsItXaerosMapChestTrakerIntegrationPlugin implements WhereIsItClientPlugin {
    private static final String SUFFIX = " [CT]";
    private static final int COLOR_ID = 0;
    private static boolean running = false;
    private static Item searchItem = null;

    private static Item getItemFromSearchRequest(SearchRequest request) {
        NbtCompound packedRequest = request.pack();

        if (packedRequest.contains(SearchRequest.DATA, NbtElement.LIST_TYPE)) {
            NbtList criterionList = packedRequest.getList(SearchRequest.DATA, NbtElement.COMPOUND_TYPE);
            for (NbtElement element : criterionList) {
                if (element instanceof NbtCompound compound) {
                    if (compound.contains(SearchRequest.ID, NbtElement.STRING_TYPE) &&
                            "whereisit:item".equals(compound.getString("Id"))) {
                        if (compound.contains(SearchRequest.DATA, NbtElement.COMPOUND_TYPE)) {
                            NbtCompound data = compound.getCompound(SearchRequest.DATA);
                            if (data.contains("ItemId", NbtElement.STRING_TYPE)) {
                                String itemId = data.getString("ItemId");
                                Identifier itemIdentifier = Identifier.tryParse(itemId);
                                if (itemIdentifier != null) {
                                    return Registries.ITEM.get(itemIdentifier);
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    public static boolean doSearch(SearchRequest request, Consumer<Collection<SearchResult>> collectionConsumer) {
        searchItem = getItemFromSearchRequest(request);
        return true;
    }

    private static void onResults(Collection<SearchResult> results) {
        if (searchItem == null) return;
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

            Optional<Memory> optionalMemory = memoryBank.getMemory(key, pos);
            if (optionalMemory.isEmpty()) continue;

            Memory memory = optionalMemory.get();
            int totalItemCount = CountUtils.countItemsOf(memory.items(), searchItem);

            String itemName = Text.translatable(searchItem.getTranslationKey()).getString();
            String waypointName = totalItemCount + " " + itemName + SUFFIX;
            String waypointLabel = totalItemCount <= 99 ? String.valueOf(totalItemCount) : "99";

            waypoints.add(new Waypoint(pos.getX(), pos.getY(), pos.getZ(),
                    waypointName, waypointLabel, COLOR_ID, 0, true));
        }
    }

    private static void onResultsCleared() {
        if (!running) return;
        running = false;
        searchItem = null;

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
        SearchInvoker.EVENT.register(WhereIsItXaerosMapChestTrakerIntegrationPlugin::doSearch);
        OnResult.EVENT.register(WhereIsItXaerosMapChestTrakerIntegrationPlugin::onResults);
        OnResultsCleared.EVENT.register(WhereIsItXaerosMapChestTrakerIntegrationPlugin::onResultsCleared);
    }
}
