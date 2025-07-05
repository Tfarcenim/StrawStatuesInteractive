package tfar.strawstatuesinteractive.client;

import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.network.chat.Component;

public class CommandListScreen extends AbstractConfiguringScreen{
    public CommandListScreen(Component title, StrawStatue strawStatue) {
        super(title);
        this.strawStatue = strawStatue;
    }
}
