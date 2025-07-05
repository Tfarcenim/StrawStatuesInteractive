package tfar.strawstatuesinteractive;

import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public interface StrawStatueDuck {

    @Nullable Player getTalkingTo();

    void setTalkingTo(@Nullable Player player);

    @Nullable Dialogue getDialogue();

    void setDialogue(Dialogue dialogue);

    static StrawStatueDuck of(StrawStatue strawStatue) {
        return (StrawStatueDuck)(Object) strawStatue;
    }
}
