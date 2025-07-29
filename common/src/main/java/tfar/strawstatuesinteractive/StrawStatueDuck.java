package tfar.strawstatuesinteractive;

import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public interface StrawStatueDuck {

    @Nullable Player getTalkingTo();

    void setTalkingTo(@Nullable Player player);

    @Nullable Dialogue getDialogue();

    void setDialogue(Dialogue dialogue);

    static StrawStatueDuck of(ArmorStand strawStatue) {
        return (StrawStatueDuck)(Object) strawStatue;
    }
}
