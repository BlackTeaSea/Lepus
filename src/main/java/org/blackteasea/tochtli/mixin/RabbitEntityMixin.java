package org.blackteasea.tochtli.mixin;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(RabbitEntity.class)
public abstract class RabbitEntityMixin extends AnimalEntity implements Tameable {

    private static final TrackedData<Byte> TAMEABLE_FLAGS = DataTracker.registerData(RabbitEntityMixin.class, TrackedDataHandlerRegistry.BYTE);

    private static final TrackedData<Optional<LazyEntityReference<LivingEntity>>> OWNER_UUID = DataTracker.registerData(RabbitEntityMixin.class, TrackedDataHandlerRegistry.LAZY_ENTITY_REFERENCE);


    @Shadow public abstract boolean isBreedingItem(ItemStack stack);

    protected RabbitEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        setTamed(false, false);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    protected void onInitDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(TAMEABLE_FLAGS, (byte) 0);
        builder.add(OWNER_UUID, Optional.empty());
    }

    public void setTamed(boolean tamed, boolean updateAttributes) {
        byte b = (Byte) this.dataTracker.get(TAMEABLE_FLAGS);
        if (tamed) {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte) (b | 4));
        } else {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte) (b & -5));
        }
        if (updateAttributes) {
            this.updateTamedAttributes();
        }
    }

    public boolean isTamed() {
        return ((Byte) this.dataTracker.get(TAMEABLE_FLAGS) & 4) != 0;
    }

    protected void updateTamedAttributes() {
        if (this.isTamed()) {
            this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(20.0D);
        } else {
            this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(8.0D);
        }
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(owner).map(LazyEntityReference::new));
    }
    public void setTamedBy(PlayerEntity player) {
        this.setTamed(true, true);
        this.setOwner(player);
        if (player instanceof ServerPlayerEntity serverPlayerEntity) {
            Criteria.TAME_ANIMAL.trigger(serverPlayerEntity, this);
        }
    }

    private void tryTame(PlayerEntity player) {
        if (this.random.nextInt(3) == 0) {
            this.setTamedBy(player);
//            LOGGER.info("RabbitEntityMixin: tryTame called");
            this.getWorld().sendEntityStatus(this, (byte) 7);
        } else {
            this.getWorld().sendEntityStatus(this, (byte) 6);
        }
    }
//    Logger LOGGER = LoggerFactory.getLogger("Tochtli");
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (this.isTamed()){
            return ActionResult.PASS;
        }

        if (this.isBreedingItem(itemStack)) {
//            LOGGER.info("RabbitEntityMixin: onInteractMob called");
            if (!this.getWorld().isClient()){
                this.eat(player, hand, itemStack);
                this.tryTame(player);
                this.setPersistent();
                this.playEatSound();
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.SUCCESS;
    }
}
