package tfar.strawstatuesinteractive.mixin;

import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.strawstatuesinteractive.Dialogue;
import tfar.strawstatuesinteractive.StrawStatueDuck;
import tfar.strawstatuesinteractive.StrawStatuesInteractiveForge;
import tfar.strawstatuesinteractive.network.SetDialoguePacket;
import tfar.strawstatuesinteractive.platform.Services;

import javax.annotation.Nullable;

@Mixin(StrawStatue.class)
abstract class StrawStatueMixin extends ArmorStand implements StrawStatueDuck {
    @Nullable
    private Player talkingTo;

    @Nullable Dialogue dialogue;

    public StrawStatueMixin(EntityType<? extends ArmorStand> entityType, Level level) {
        super(entityType, level);
    }


    @Override
    public Player getTalkingTo() {
        return talkingTo;
    }

    @Override
    public void setTalkingTo(@Nullable Player talkingTo) {
        this.talkingTo = talkingTo;
    }

    @Override
    public void setDialogue(Dialogue dialogue) {
        this.dialogue = dialogue;
        if (!level().isClientSide) {
            SetDialoguePacket.syncDialogues(this,dialogue);
        }
    }

    @Nullable
    @Override
    public Dialogue getDialogue() {
        return dialogue;
    }

    @Inject(method = "readAdditionalSaveData",at = @At("RETURN"))
    void readExtraData(CompoundTag tag, CallbackInfo ci) {
        StrawStatuesInteractiveForge.readExtraData((StrawStatue) (Object)this,tag);
    }

    @Inject(method = "addAdditionalSaveData",at = @At("RETURN"))
    void saveExtraData(CompoundTag tag, CallbackInfo ci) {
        StrawStatuesInteractiveForge.saveExtraData((StrawStatue) (Object)this,tag);
    }

    @Inject(method = "onUseEntityAt",at = @At("HEAD"),remap = false,cancellable = true)
    private static void yes(Player player, Level level, InteractionHand interactionHand, Entity target, Vec3 hitVector, CallbackInfoReturnable<EventResultHolder<InteractionResult>> cir) {
        StrawStatuesInteractiveForge.yes(player, level, interactionHand, target, hitVector, cir);
    }
}
