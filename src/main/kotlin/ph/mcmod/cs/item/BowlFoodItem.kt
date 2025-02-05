package ph.mcmod.cs.item

import net.minecraft.advancement.criterion.Criteria
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsage
import net.minecraft.item.PotionItem
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.stat.Stats
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.UseAction
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

class BowlFoodItem(settings: Settings) : Item(settings) {
    override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
        user.eatFood(world,stack)
        if (user is PlayerEntity) {
            if (user is ServerPlayerEntity) {
                Criteria.CONSUME_ITEM.trigger(user, stack)
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this))
            ItemUsage.exchangeStack(stack, user, Items.BOWL.defaultStack)
        }
        world.emitGameEvent(user, GameEvent.EAT, user.cameraBlockPos)
        return stack
    }

    override fun use(world: World?, user: PlayerEntity?, hand: Hand?): TypedActionResult<ItemStack?>? {
        return ItemUsage.consumeHeldItem(world, user, hand)
    }
}