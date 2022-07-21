package com.github.kevindagame.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class SearchBarMixin1_18 extends Screen {
    @Shadow
    private ServerList serverList;
    @Shadow
    protected abstract void refresh();
    @Shadow
    protected MultiplayerServerListWidget serverListWidget;
    private String filterText = "";
    protected SearchBarMixin1_18(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void addSearchBar(CallbackInfo ci) {
        var filterField = new TextFieldWidget(this.textRenderer, 8, 10, 100, 20, new TranslatableText("addServer.enterName"));
        filterField.setText(filterText);
        filterField.setChangedListener((text) -> {
            filterText = text;
            filter();
        });
        this.addDrawableChild(filterField);
    }
    @Inject(method = "render", at = @At("RETURN"))
    private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci){
        drawTextWithShadow(matrices, this.textRenderer, new TranslatableText("Filter server list"), 8, 0, 10526880);
    }

    private void filter() {
        var serverList = new ServerList(this.client);
        for (int i = serverList.size()- 1; i >= 0; i--) {
            var server = serverList.get(i);
            if (!server.name.contains(filterText) && !server.address.contains(filterText)) {
                serverList.remove(server);
            }
        }
        this.serverListWidget.setServers(serverList);
    }
}
