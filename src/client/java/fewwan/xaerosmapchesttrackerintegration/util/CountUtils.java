package fewwan.xaerosmapchesttrackerintegration.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class CountUtils {

    public static int countItemsOf(List<ItemStack> items, Item searchItem) {
        return items.stream()
                .mapToInt(stack -> countItemInStack(stack, searchItem))
                .sum();
    }

    private static int countItemInStack(ItemStack stack, Item searchItem) {
        int count = 0;

        if (stack.getItem() == searchItem) {
            count += stack.getCount();
        }

        ContainerComponent containerComponent = stack.get(DataComponentTypes.CONTAINER);
        if (containerComponent != null) {
            count += countItemsInContainer(containerComponent, searchItem) * stack.getCount();
        }

        return count;
    }

    private static int countItemsInContainer(ContainerComponent container, Item searchItem) {
        return container.streamNonEmpty()
                .mapToInt(stack -> countItemInStack(stack, searchItem))
                .sum();
    }
}
