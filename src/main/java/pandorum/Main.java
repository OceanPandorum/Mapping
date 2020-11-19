package pandorum;

import arc.Core;
import arc.files.Fi;
import arc.util.CommandHandler;
import arc.util.Log;
import arc.util.Strings;
import arc.util.Structs;
import components.Bundle;
import components.Config;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.game.Teams;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.mod.Plugin;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.*;

public class Main extends Plugin{
    public static final Fi dir = Core.settings.getDataDirectory().child("/mods/pandorum/");
    public static final Config config = new Config();
    public static final Bundle bundle = new Bundle();


    public Main(){}

    @Override
    public void registerClientCommands(CommandHandler handler){

        //Заспавнить блок
        handler.<Player>register(bundle.get("block.set.name"), bundle.get("block.set.params"), bundle.get("block.set.description"), (args, player) -> Map.setBlock(player, args));

        //Заспавнить руду
        handler.<Player>register(bundle.get("spawn.ore.name"), bundle.get("spawn.ore.params"), bundle.get("spawn.ore.description"), (args, player) -> {
            Map.spawnOre(player, args);
            Info.text(player, "$success.spawn.ore");
        });

        //Задеспавнить юнитов
        handler.register(bundle.get("despw.name"), bundle.get("despw.description"), args -> {
            Groups.unit.each(Unit::kill);
            Log.info(bundle.get("despw.log"));
        });

        //Заспавнить юнитов
        handler.<Player>register(bundle.get("spawn.name"), bundle.get("spawn.params"), bundle.get("spawn.description"), (args, player) -> {
            if(!Strings.canParseInt(args[1])){
                Info.text(player, "$commands.count-not-int");
                return;
            }

            UnitType unit = content.units().find(b -> b.name.equalsIgnoreCase(args[0]));
            if(unit == null){
                Info.text(player, "$spawn.units");
                return;
            }

            int count = Strings.parseInt(args[1]);

            Team team = Structs.find(Team.baseTeams, t -> t.name.equalsIgnoreCase(args[2]));
            if(team == null){
                Info.text(player, "$teamp.teams");
                return;
            }

            for(int i = 0; i < count; i++){
                unit.spawn(team, player.x, player.y);
            }
            Info.bundled(player, "spawn.ok", count, unit.name);

        });

        //Заспавнить ядро (попытка искоренить шнеки)
        handler.<Player>register(bundle.get("core.name"), bundle.get("core.params"), bundle.get("core.description"), (args, player) -> {

            Block core = switch(args[0]){
                case "medium" -> Blocks.coreFoundation;
                case "big" -> Blocks.coreNucleus;
                default -> Blocks.coreShard;
            };

            Call.constructFinish(world.tile(player.tileX(), player.tileY()), core, player.unit(), (byte) 0, player.team(), false);

            Info.text(player, world.tile(player.tileX(), player.tileY()).block() == core ? "$core.yes" : "$core.no");
        });

        //Выход в Хаб
        handler.<Player>register(bundle.get("hub.name"), bundle.get("hub.description"), (args, player) -> Call.connect(player.con, config.object.getString("hub-ip", null), config.object.getInt("hub-port", 0)));

        //Выдача предметов в ядро
        handler.<Player>register(bundle.get("give.name"), bundle.get("give.params"), bundle.get("give.description"), (args, player) -> {

            if(!Strings.canParseInt(args[0])){
                Info.text(player, "$commands.count-not-int");
                return;
            }

            int count = Strings.parseInt(args[0]);

            Item item = content.items().find(b -> b.name.equalsIgnoreCase(args[1]));
            if(item == null){
                Info.text(player, "$give.item-not-found");
                return;
            }

            Teams.TeamData team = state.teams.get(player.team());
            if(!team.hasCore()){
                Info.text(player, "$give.core-not-found");
                return;
            }
            CoreBlock.CoreBuild core = team.cores.first();

            for(int i = 0; i < count; i++){
                core.items.set(item, count);
            }
            Info.text(player, "$give.success");
        });

        //Cliffs
        handler.<Player>register(bundle.get("cliff.name"), bundle.get("cliff.description"), (args, player) -> Map.addCliffs());

        handler.<Player>register(bundle.get("wall.name"), bundle.get("wall.description"), (args, player) -> {
            int x = Math.round(player.x/8);
            int y = Math.round(player.y/8);

            Block blocks = Blocks.stoneWall;

            if (blocks != null) {
                Vars.world.tile(x, y).setBlock(blocks);
                Vars.world.tile(x, y).setTeam(player.team());

                Call.worldDataBegin();
                Groups.player.each(p -> Vars.netServer.sendWorldData(p));
            }
        });

    }
}
