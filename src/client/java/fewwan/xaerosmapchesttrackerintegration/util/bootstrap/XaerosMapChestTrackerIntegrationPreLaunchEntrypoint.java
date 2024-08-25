package fewwan.xaerosmapchesttrackerintegration.util.bootstrap;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class XaerosMapChestTrackerIntegrationPreLaunchEntrypoint implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        MixinExtrasBootstrap.init();
    }
}
