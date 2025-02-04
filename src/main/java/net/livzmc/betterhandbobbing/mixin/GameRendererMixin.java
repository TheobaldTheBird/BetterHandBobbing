package net.livzmc.betterhandbobbing.mixin;

import net.livzmc.betterhandbobbing.BetterHandBobbing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final private MinecraftClient client;

    /**
     * I take out the part of code that moves the hand. Essentially separating that piece of code into it's own option.
     */
    @Inject(at = @At("HEAD"), method = "bobView", cancellable = true)
    private void bhb$bobView(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (this.client.getCameraEntity() instanceof PlayerEntity playerEntity) {
            float f = playerEntity.horizontalSpeed - playerEntity.prevHorizontalSpeed;
            float g = -(playerEntity.horizontalSpeed + f * tickDelta);
            float h = MathHelper.lerp(tickDelta, playerEntity.prevStrideDistance, playerEntity.strideDistance);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin(g * 3.1415927F) * h * 3.0F));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Math.abs(MathHelper.cos(g * 3.1415927F - 0.2F) * h) * 5.0F));
        }
        ci.cancel();
    }

    @Inject(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getBobView()Lnet/minecraft/client/option/SimpleOption;", shift = At.Shift.AFTER))
    private void inject(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci) {
        if (BetterHandBobbing.getHandBob().getValue()) {
            BetterHandBobbing.handBob(matrices, tickDelta, this.client);
        }
    }
}
