package me.jellysquid.mods.sodium.mixin;
import me.jellysquid.mods.sodium.SodiumMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.packet.s2c.play.VelocityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onVelocityUpdate", at = @At("HEAD"), cancellable = true)
    private void onVelocityUpdate(VelocityUpdateS2CPacket packet, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!SodiumMod.isEnabled || mc.player == null || mc.world == null) return;
        if (packet.getId() == mc.player.getEntityId()) {
            boolean allowKnockback = false;
            for (Entity entity : mc.world.getEntities()) {
                double distance = mc.player.squaredDistanceTo(entity);
                if ((entity instanceof EndCrystalEntity || entity instanceof TntEntity) && distance < 144.0) {
                    allowKnockback = true;
                    break;
                }
                if (entity instanceof FishingBobberEntity && distance < 16.0) {
                    allowKnockback = true;
                    break;
                }
            }
            if (!allowKnockback) {
                ci.cancel(); 
            }
        }
    }
}
