package me.odinclient.mixin.mixins;

import me.odinclient.features.impl.skyblock.CancelInteract;
import me.odinmain.events.impl.ClickEvent;
import me.odinmain.events.impl.PreKeyInputEvent;
import me.odinmain.events.impl.PreMouseInputEvent;
import me.odinmain.features.impl.render.CPSDisplay;
import me.odinmain.utils.EventExtensions;
import me.odinmain.utils.skyblock.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {Minecraft.class}, priority = 800)
public class MixinMinecraft {

    @Shadow public EntityPlayerSP thePlayer;

    @Inject(method = {"runTick"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;dispatchKeypresses()V")})
    public void keyPresses(CallbackInfo ci) {
        int k = (Keyboard.getEventKey() == 0) ? (Keyboard.getEventCharacter() + 256) : Keyboard.getEventKey();
        if (Keyboard.getEventKeyState()) EventExtensions.postAndCatch(new PreKeyInputEvent(k));
    }

    @Inject(method = {"runTick"}, at = {@At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventButton()I", remap = false)})
    public void mouseKeyPresses(CallbackInfo ci) {
        if (Mouse.getEventButtonState()) EventExtensions.postAndCatch(new PreMouseInputEvent(Mouse.getEventButton()));
    }

    @Inject(method = {"runTick"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;handleInput()V")})
    private void handleInput(CallbackInfo ci) {
        PlayerUtils.INSTANCE.handleWindowClickQueue();
    }

    @Inject(method = "rightClickMouse", at = @At("HEAD"), cancellable = true)
    private void rightClickMouse(CallbackInfo ci) {
        CPSDisplay.INSTANCE.onRightClick();
        if (EventExtensions.postAndCatch(new ClickEvent.RightClickEvent())) {
            ci.cancel();
            return;
        }
    }

    @Inject(method = "clickMouse", at = @At("HEAD"), cancellable = true)
    private void clickMouse(CallbackInfo ci)
    {
        CPSDisplay.INSTANCE.onLeftClick();
        if (EventExtensions.postAndCatch(new ClickEvent.LeftClickEvent())) ci.cancel();
    }

    @Redirect(method = {"rightClickMouse"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;isAirBlock(Lnet/minecraft/util/BlockPos;)Z"))
    public boolean shouldCancelInteract(WorldClient instance, BlockPos blockPos) {
        return CancelInteract.INSTANCE.cancelInteractHook(instance, blockPos);
    }
}