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
                Text.literal("Error Xaero's Map Chest Tracker Integration").setStyle(Style.EMPTY.withColor(Formatting.RED)),
                Text.literal("This mod requires Xaero's Minimap to work properly."),
                Text.literal("Download"),
                Text.literal("Close")
        ));
    }
}
