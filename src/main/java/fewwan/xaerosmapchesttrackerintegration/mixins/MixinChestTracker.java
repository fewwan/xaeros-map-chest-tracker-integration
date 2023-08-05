package fewwan.xaerosmapchesttrackerintegration.mixins;

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
import fewwan.xaerosmapchesttrackerintegration.XaerosMapChestTrackerIntegration;
import xaero.common.XaeroMinimapSession;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointSet;
import xaero.common.minimap.waypoints.WaypointsManager;

import java.util.List;

import static fewwan.xaerosmapchesttrackerintegration.utils.Utils.countItemsOf;
import static xaero.common.settings.ModSettings.COLORS;

@Mixin(red.jackf.chesttracker.ChestTracker.class)
public class MixinChestTracker {
    @Inject(method = "searchForItem", at = @At("HEAD"))
    private static void injectDebugMessage(ItemStack stack, CallbackInfo ci) {
        MemoryDatabase database = MemoryDatabase.getCurrent();
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        Item searchItem = stack.getItem();
        XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        if (minimapSession == null) return;
        final WaypointsManager waypointsManager = minimapSession.getWaypointsManager();
        WaypointSet waypointSet = waypointsManager.getWaypoints();
        if (waypointSet == null) return;
        final List<Waypoint> waypoints = waypointSet.getList();
        if (database != null && world != null) {
            List<Memory> memories = database.findItems(stack, world.getRegistryKey().getValue());
            memories.forEach(memory -> {
                BlockPos position = memory.getPosition();
                List<ItemStack> items = memory.getItems();
                int totalItemCount = countItemsOf(items, searchItem);

                int x = position.getX();
                int y = position.getY();
                int z = position.getZ();

                XaerosMapChestTrackerIntegration.LOGGER.info("Debug message: Found " + totalItemCount +  " items of " + Text.translatable(stack.getTranslationKey()).getString() + " at " + x + ", " + y + ", " + z);
                waypoints.add(new Waypoint(
                        x,
                        y,
                        z,
                        Text.translatable(stack.getTranslationKey()).getString() + " " + String.valueOf(totalItemCount) + " [CT]",
                        totalItemCount <= 99 ?  String.valueOf(totalItemCount) : "99",
                        0 % COLORS.length,
                        0,
                        true
                ));
            });
        }
    }
}
