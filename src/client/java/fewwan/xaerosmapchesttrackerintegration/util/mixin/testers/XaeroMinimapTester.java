package fewwan.xaerosmapchesttrackerintegration.util.mixin.testers;

import fewwan.xaerosmapchesttrackerintegration.util.ModCheckUtils;
import me.fallenbreath.conditionalmixin.api.mixin.ConditionTester;

public class XaeroMinimapTester implements ConditionTester {
    @Override
    public boolean isSatisfied(String mixinClassName) {
        return ModCheckUtils.isXaeroMinimapModLoaded();
    }
}
