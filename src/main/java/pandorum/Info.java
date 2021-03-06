package pandorum;

import arc.util.Strings;
import mindustry.gen.Player;

import static pandorum.Main.bundle;

public class Info{

    private Info(){
        // Util class
    }


    public static void text(Player player, String key){
        player.sendMessage(key.startsWith("$") ? bundle.get(key.substring(1)) : key);
    }

    public static void bundled(Player player, String key, Object... values){
        player.sendMessage(bundle.format(key, values));
    }

    public static void format(Player player, String key, Object... values){
        player.sendMessage(Strings.format(key, values));
    }
}
