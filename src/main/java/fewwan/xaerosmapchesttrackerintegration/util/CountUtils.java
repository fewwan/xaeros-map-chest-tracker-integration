/*
 * This file is part of the "Xaero's Map Chest Tracker Integration" project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * "Xaero's Map Chest Tracker Integration" is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Xaero's Map Chest Tracker Integration" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with "Xaero's Map Chest Tracker Integration".  If not, see <https://www.gnu.org/licenses/>.
 */

package fewwan.xaerosmapchesttrackerintegration.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CountUtils {
    static final Set<Item> shulkerBoxes = Set.of(
            Items.SHULKER_BOX,
            Items.WHITE_SHULKER_BOX,
            Items.LIGHT_GRAY_SHULKER_BOX,
            Items.GRAY_SHULKER_BOX,
            Items.BLACK_SHULKER_BOX,
            Items.RED_SHULKER_BOX,
            Items.ORANGE_SHULKER_BOX,
            Items.YELLOW_SHULKER_BOX,
            Items.LIME_SHULKER_BOX,
            Items.GREEN_SHULKER_BOX,
            Items.CYAN_SHULKER_BOX,
            Items.LIGHT_BLUE_SHULKER_BOX,
            Items.BLUE_SHULKER_BOX,
            Items.PURPLE_SHULKER_BOX,
            Items.MAGENTA_SHULKER_BOX,
            Items.PINK_SHULKER_BOX,
            Items.BROWN_SHULKER_BOX
    );

    public static List<ItemStack> getItemsInsideShulkerBox(ItemStack shulkerBox) {
        List<ItemStack> itemsInside = new ArrayList<>();

        NbtCompound shulkerBoxNbtCompound = shulkerBox.getNbt();
        if (shulkerBoxNbtCompound != null && shulkerBoxNbtCompound.contains("BlockEntityTag", NbtElement.COMPOUND_TYPE)) {
            NbtCompound blockEntityTag = shulkerBoxNbtCompound.getCompound("BlockEntityTag");
            if (blockEntityTag.contains("Items", NbtElement.LIST_TYPE)) {
                NbtList itemsList = blockEntityTag.getList("Items", NbtElement.COMPOUND_TYPE);
                for (NbtElement itemTag : itemsList) {
                    if (itemTag instanceof NbtCompound) {
                        ItemStack itemStack = ItemStack.fromNbt((NbtCompound) itemTag);
                        itemsInside.add(itemStack);
                    }
                }
            }
        }
        return itemsInside;
    }

    public static int countItemsOf(List<ItemStack> items, Item searchItem) {
        int itemCount = 0;
        for (ItemStack stack : items) {
            if (stack.getItem() == searchItem) {
                itemCount += stack.getCount();
            } else if (shulkerBoxes.contains(stack.getItem())) {
                itemCount += countItemsOf(getItemsInsideShulkerBox(stack), searchItem) * stack.getCount();
            }
        }
        return itemCount;
    }
}
