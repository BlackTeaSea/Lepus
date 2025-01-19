package org.blackteasea.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnimalEntity.class)
public class AnimalEntityMixin {

    private static final String MOD_ID = "lepus";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Inject(at = @At("HEAD"), method = "interactMob", cancellable = true)
    private void onInteractMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (player.getMainHandStack().getItem() == Items.CARROT) {
            if (((AnimalEntity) (Object) this).getType() == EntityType.RABBIT) {
                LOGGER.info("Rabbit interacted with rabbit");
            }
            else {
                LOGGER.info("Carrot interacted with animal");
            }
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}