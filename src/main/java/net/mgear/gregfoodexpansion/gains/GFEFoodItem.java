package net.mgear.gregfoodexpansion.gains;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 参与增益结算管线的食品物品(dishes-and-gains.md §2/§2.1)。
 *
 * 进食链路:super.finishUsingItem 走完整原版流程(Player.eat 营养/统计/
 * CONSUME_ITEM 触发器、LivingEntity.eat 音效/基准效果/消耗/gameEvent,
 * 均从 1.20.1 反编译源核实),随后在服务端把效果时长升级为乘后值。
 * SoL 计数监听 LivingEntityUseItemEvent.Finish,不受本 override 影响;
 * 营养值 ≥1 满足其统计门槛(m0-content-audit.md §5.2/§5.4-2)。
 */
public class GFEFoodItem extends Item {
    public GFEFoodItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        GFEGainPipeline.applyDiversityBoost(entity, stack);
        return result;
    }
}
