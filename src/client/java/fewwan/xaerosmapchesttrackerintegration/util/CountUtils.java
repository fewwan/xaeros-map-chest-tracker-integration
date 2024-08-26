package fewwan.xaerosmapchesttrackerintegration.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.List;

public class CountUtils {
    public static List<ItemStack> getItemsInsideContainer(ItemStack containerStack) {
        List<ItemStack> itemsInside = new ArrayList<>();

        NbtCompound shulkerBoxNbtCompound = containerStack.getNbt();
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
            }
            itemCount += countItemsOf(getItemsInsideContainer(stack), searchItem) * stack.getCount();
        }
        return itemCount;
    }
}
