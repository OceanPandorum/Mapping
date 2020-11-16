package pandorum;

import arc.math.Mathf;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.world.Block;

public class Map {

    public static void spawnOre(Player player, String[] Args) {

        String[] args = Args[0].split(" ");
        Block block = Blocks.oreCopper;
        int radius = 1;

        if(!args[0].matches("\\d+")) {
            Info.text(player, "$error.spawn.ore");
        } else if(Long.parseLong(args[0]) >= 1000) {
            radius = 1000;
        } else radius = Integer.parseInt(args[0]);

        int x = Math.round(player.x/8);
        int y = Math.round(player.y/8);

        if(args.length>=2) {
            if (args[1].equals("lead")) block = Blocks.oreLead;
            if (args[1].equals("coal")) block = Blocks.oreCoal;
            if (args[1].equals("titanium")) block = Blocks.oreTitanium;
            if (args[1].equals("thorium")) block = Blocks.oreThorium;
            if (args[1].equals("scrap")) block = Blocks.oreScrap;
            if (args[1].equals("water")) block = Blocks.water;
            if (args[1].equals("deepwater")) block = Blocks.deepwater;
            if (args[1].equals("sand")) block = Blocks.sand;
            if (args[1].equals("darksand")) block = Blocks.darksand;
            if (args[1].equals("magmarock")) block = Blocks.magmarock;
            if (args[1].equals("hotrock")) block = Blocks.hotrock;
            if (args[1].equals("snow")) block = Blocks.snow;
            if (args[1].equals("tar")) block = Blocks.tar;
            if (args[1].equals("slag")) block = Blocks.slag;
            if (args[1].equals("space")) block = Blocks.space;
        }

        for(int rx = -radius; rx <= radius; rx++){
            for(int ry = -radius; ry <= radius; ry++){
                if(Mathf.dst2(rx, ry) <= (radius - 0.5f) * (radius - 0.5f)){
                    int wx = x + rx, wy = y + ry;

                    if(wx < 0 || wy < 0 || wx >= Vars.world.width() || wy >= Vars.world.height()){
                        continue;
                    }
                    Vars.world.tile(wx, wy).setOverlay(block);
                }
            }
        }

        Call.worldDataBegin();
        Groups.player.each(p -> Vars.netServer.sendWorldData(p));

    }


    public static void setBlock(Player player, String[] args) {

        int x = Math.round(player.x/8);
        int y = Math.round(player.y/8);

        Block blocks = Vars.content.blocks().find(b -> b.name.equals(args[0]));

        if (blocks != null) {
            Info.text(player, "$block.set.success");
            Vars.world.tile(x, y).setBlock(blocks);
            Vars.world.tile(x, y).setTeam(player.team());

            Call.worldDataBegin();
            Groups.player.each(p -> Vars.netServer.sendWorldData(p));

        } else Info.text(player, "$block.set.error");
    }
}