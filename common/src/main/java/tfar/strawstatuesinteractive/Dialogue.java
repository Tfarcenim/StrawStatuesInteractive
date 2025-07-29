package tfar.strawstatuesinteractive;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public record Dialogue(List<String> pages, List<NPCCommandEntry> commands) {

    public static final String KEY = StrawStatuesInteractive.id("dialogue").toString();

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        for (String s : pages) {
            listTag.add(StringTag.valueOf(s));
        }
        tag.put("pages",listTag);
        ListTag commandTag = new ListTag();
        for (NPCCommandEntry commandEntry : commands) {
            commandTag.add(commandEntry.toTag());
        }

        tag.put("commands",commandTag);

        return tag;
    }

    public static Dialogue load(CompoundTag tag) {
        ListTag pages1 = tag.getList("pages", Tag.TAG_STRING);
        List<String> strings = new ArrayList<>();
        for (Tag t : pages1) {
            strings.add(t.getAsString());
        }

        ListTag commands1 = tag.getList("commands", Tag.TAG_COMPOUND);
        List<NPCCommandEntry> npcCommandEntries = new ArrayList<>();
        for (Tag t : commands1) {
            npcCommandEntries.add(NPCCommandEntry.fromTag((CompoundTag) t));
        }

        return new Dialogue(strings,npcCommandEntries);
    }

    public void toPacket(FriendlyByteBuf buf) {
        buf.writeCollection(pages, FriendlyByteBuf::writeUtf);
        buf.writeCollection(commands,(buf1, npcCommandEntry) -> npcCommandEntry.toPacket(buf1));
    }

    public static Dialogue fromPacket(FriendlyByteBuf buf) {
        return new Dialogue(buf.readList(FriendlyByteBuf::readUtf),buf.readList(NPCCommandEntry::fromPacket));
    }
}
