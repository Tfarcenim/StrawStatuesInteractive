package tfar.strawstatuesinteractive;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public class NPCCommandEntry {

    public static final int MODE = 1;
    public static final int ENTER = 1 << 1;
    public static final int EXIT = 1 << 2;

    public List<String> commands = new ArrayList<>();
    public boolean buttonMode;
    public boolean onEnter;
    public boolean onExit;
    public String name = "";

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        ListTag listTag = new ListTag();

        commands.forEach(s -> listTag.add(StringTag.valueOf(s)));

        tag.put("commands",listTag);

        tag.putBoolean("button_mode",buttonMode);
        tag.putBoolean("on_enter",onEnter);
        tag.putBoolean("on_exit",onExit);
        tag.putString("name",name);
        return tag;
    }

    public void toPacket(FriendlyByteBuf buf) {
        buf.writeCollection(commands,FriendlyByteBuf::writeUtf);
        buf.writeBoolean(buttonMode);
        buf.writeBoolean(onEnter);
        buf.writeBoolean(onExit);
        buf.writeUtf(name);
    }

    public static NPCCommandEntry fromTag(CompoundTag tag) {
        var entry = new NPCCommandEntry();
        ListTag listTag = tag.getList("commands", Tag.TAG_STRING);
        for (Tag tag1 : listTag) {
            entry.commands.add(tag1.getAsString());
        }
        entry.buttonMode = tag.getBoolean("button_mode");
        entry.onEnter = tag.getBoolean("on_enter");
        entry.onExit = tag.getBoolean("on_exit");
        entry.name = tag.getString("name");

        return entry;
    }

    public static NPCCommandEntry fromPacket(FriendlyByteBuf buf) {
        var entry  = new NPCCommandEntry();
        entry.commands.addAll(buf.readList(FriendlyByteBuf::readUtf));
        entry.buttonMode = buf.readBoolean();
        entry.onEnter = buf.readBoolean();
        entry.onExit = buf.readBoolean();
        entry.name = buf.readUtf();
        return entry;
    }
}
