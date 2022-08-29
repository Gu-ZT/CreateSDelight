package ph.mcmod.cs.mixin;

import com.simibubi.create.content.contraptions.processing.BasinTileEntity;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.inventory.InvManipulationBehaviour;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import ph.mcmod.cs.MixinDelegates;
import ph.mcmod.cs.game.FTemperature;
import ph.mcmod.cs.game.InjectBasinTileEntity;

import java.util.Iterator;
@Mixin(value = BasinTileEntity.class, remap = false)
public abstract class MixinBasinTileEntity extends SmartTileEntity implements FTemperature {
    private double temperature = 25;
    private double animationTicks = 0;
    private Storage<ItemVariant> targetInv;
    private Transaction nested;

    public MixinBasinTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public double getTemperature() {
        return temperature;
    }

    @Override
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    @Override
    public double getAnimationTicks() {
        return animationTicks;
    }

    @Override
    public void setAnimationTicks(double animationTicks) {
        this.animationTicks = animationTicks;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickBoil(CallbackInfo ci) {
        MixinDelegates.tickBoil((BasinTileEntity) (Object) this);
    }

    @Inject(method = "write", at = @At("HEAD"))
    private void write(NbtCompound compound, boolean clientPacket, CallbackInfo ci) {
        compound.putDouble("temperature", getTemperature());
    }

    @Inject(method = "read", at = @At("HEAD"))
    private void read(NbtCompound compound, boolean clientPacket, CallbackInfo ci) {
        setTemperature(compound.getDouble("temperature"));
    }

    @Inject(method = "tryClearingSpoutputOverflow", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/transfer/v1/item/ItemVariant;of(Lnet/minecraft/item/ItemStack;)Lnet/fabricmc/fabric/api/transfer/v1/item/ItemVariant;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void captureLocals(CallbackInfo ci, BlockState blockState, Direction direction, BlockEntity te, FilteringBehaviour filter, InvManipulationBehaviour inserter, Storage<ItemVariant> targetInv, Storage<FluidVariant> targetTank, boolean update, Transaction t, Iterator<ItemStack> iterator, ItemStack itemStack, Transaction nested) {
        this.targetInv = targetInv;
        this.nested = nested;
    }

    @Redirect(method = "tryClearingSpoutputOverflow", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/transfer/v1/storage/Storage;insert(Ljava/lang/Object;JLnet/fabricmc/fabric/api/transfer/v1/transaction/TransactionContext;)J", ordinal = 0))
    private long modifyOutput(Storage<ItemVariant> targetInv, Object itemVariant, long amount, TransactionContext nested) {
        return InjectBasinTileEntity.modifyOutput((BasinTileEntity) (Object) this,targetInv,(ItemVariant) itemVariant,amount, nested);
    }

}
