package fewwan.xaerosmapchesttrackerintegration.mixin.client;

import fewwan.xaerosmapchesttrackerintegration.util.ModCheckUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;
import java.net.URISyntaxException;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {

    protected MixinTitleScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void NoMinimapFoundScreen(CallbackInfo ci) {
        if (ModCheckUtils.isXaeroMinimapModLoaded()) return;

        MinecraftClient.getInstance().setScreen(new ConfirmScreen(
                confirmed -> {
                    if (confirmed) {
                        try {
                            Util.getOperatingSystem().open(new URI("https://www.curseforge.com/minecraft/mc-mods/xaeros-minimap"));
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        MinecraftClient.getInstance().stop();
                    }
                },
                Text.translatable("xaeros-map-chest-tracker-integration.noMinimapFound.title").setStyle(Style.EMPTY.withColor(Formatting.RED)),
                Text.translatable("xaeros-map-chest-tracker-integration.noMinimapFound.message"),
                Text.translatable("xaeros-map-chest-tracker-integration.noMinimapFound.yesText"),
                Text.translatable("xaeros-map-chest-tracker-integration.noMinimapFound.noText")
        ));
    }
}
