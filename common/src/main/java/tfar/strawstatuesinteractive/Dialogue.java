package tfar.strawstatuesinteractive;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public record Dialogue(List<String> pages) {

    public static final String KEY = StrawStatuesInteractive.id("dialogue").toString();

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        for (String s : pages) {
            listTag.add(StringTag.valueOf(s));
        }
        tag.put("pages",listTag);
        return tag;
    }

    public static Dialogue load(CompoundTag tag) {
        ListTag pages1 = tag.getList("pages", Tag.TAG_STRING);
        List<String> strings = new ArrayList<>();
        for (Tag t : pages1) {
            strings.add(t.getAsString());
        }
        return new Dialogue(strings);
    }

    public void toPacket(FriendlyByteBuf buf) {
        buf.writeCollection(pages, FriendlyByteBuf::writeUtf);
    }

    public static Dialogue fromPacket(FriendlyByteBuf buf) {
        return new Dialogue(buf.readList(FriendlyByteBuf::readUtf));
    }
}
