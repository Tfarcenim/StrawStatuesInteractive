package tfar.strawstatuesinteractive.client;

import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.network.chat.Component;

public class PreviewDialogueScreen extends DialogueScreen{
    public PreviewDialogueScreen(Component title, StrawStatue strawStatue) {
        super(title, strawStatue);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }
}
