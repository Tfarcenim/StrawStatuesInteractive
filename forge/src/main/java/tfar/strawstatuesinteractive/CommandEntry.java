package tfar.strawstatuesinteractive;

import java.util.ArrayList;
import java.util.List;

public class CommandEntry {

    public static final int MODE = 1;
    public static final int ENTER = 1 << 1;
    public static final int EXIT = 1 << 2;

    public List<String> commands = new ArrayList<>();
    public boolean buttonMode;
    public boolean onEnter;
    public boolean onExit;


}
