package tfar.strawstatuesinteractive.client;

import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import tfar.strawstatuesinteractive.StrawStatueDuck;
import tfar.strawstatuesinteractive.StrawStatuesInteractive;

public class AbstractConfiguringScreen extends AbstractScreen {
    public StrawStatue strawStatue;

    protected AbstractConfiguringScreen(Component title) {
        super(title);
    }

    public boolean stillValid(Player player) {
        return StrawStatueDuck.of(strawStatue).getTalkingTo() == player;
    }

    public void setStrawStatue(StrawStatue strawStatue) {
        this.strawStatue = strawStatue;
    }
}
