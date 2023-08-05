package fewwan.xaerosmapchesttrackerintegration.mixins;

import fewwan.xaerosmapchesttrackerintegration.XaerosMapChestTrackerIntegration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.XaeroMinimapSession;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointSet;
import xaero.common.minimap.waypoints.WaypointsManager;

import java.util.List;

@Mixin(red.jackf.whereisit.client.RenderUtils.class)
public class MixinClearSearch {
    @Inject(method = "clearSlotSearch", at = @At("HEAD"), remap = false)
    private static void clearWaypoints(CallbackInfo ci) {
        XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        if (minimapSession == null) return;
        final WaypointsManager waypointsManager = minimapSession.getWaypointsManager();
        WaypointSet waypointSet = waypointsManager.getWaypoints();
        if (waypointSet == null) return;
        final List<Waypoint> waypoints = waypointSet.getList();

        waypoints.removeIf(waypoint -> waypoint.isTemporary() && waypoint.getName().endsWith(" [CT]"));
    }
}
