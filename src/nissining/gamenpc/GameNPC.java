package nissining.gamenpc;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.ChunkUnloadEvent;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.MainLogger;
import nissining.gamenpc.gameUtils.GameAPI;
import nissining.gamenpc.entities.Npc;

import java.io.File;
import java.util.*;

public class GameNPC extends PluginBase implements Listener {

    public static boolean isHide = false;

    @Override
    public void onEnable() {
        if (!getDataFolder().mkdirs()) {
            debug("GameNPC By Nissining; Have Fun!");
        }
        if (!new File(getDataFolder(), "npc").mkdirs()) {
            debug("");
        }
        Config config = new Config(getDataFolder() + "/config.yml", 2, new ConfigSection() {{
            put("靠近NPC时隐身", true);
        }});
        isHide = config.getBoolean("靠近NPC时隐身");
        spawnAllNpc();
        getServer().getPluginManager().registerEvents(this, this);
    }

    public static void debug(String d) {
        MainLogger.getLogger().warning(d);
    }

    private String getStrPos(Position pos) {
        return pos.getFloorX() + "/" + pos.getFloorY() + "/" + pos.getFloorZ() + "/" + pos.getLevel().getFolderName();
    }

    private Position getPos(String k) {
        String[] ss = k.split("/");
        return new Position(
                Double.parseDouble(ss[0]),
                Double.parseDouble(ss[1]),
                Double.parseDouble(ss[2]),
                getServer().getLevelByName(ss[3])
        );
    }

    private Config getNpcConfig(String npcName) {
        File f = new File(getDataFolder(), "npc");
        return new Config(f + "/" + npcName + ".yml", Config.YAML);
    }

    private ConfigSection getNpcData(String npcName, Position pos, String gameName) {
        return new ConfigSection() {{
            put("npcName", npcName);
            put("npcId", 38);
            put("pos", getStrPos(pos));
            put("cmd", new ArrayList<String>());
            put("msg", "未修改");
            put("game", gameName);
        }};
    }

    private void createNpc(Position pos, String npcName, String gameName) {
        Config c = getNpcConfig(npcName);
        c.setAll(getNpcData(npcName, pos, gameName));
        c.save();
        debug("NPC:" + npcName + "已创建");
    }

    private void spawnAllNpc() {
        File npcF = new File(getDataFolder(), "npc");
        File[] fs = npcF.listFiles();
        if (fs != null) {
            int i = 0;
            for (File f : fs) {
                String fn = f.getName().substring(0, f.getName().indexOf(".yml"));
                if (fn.isEmpty())
                    continue;

                spawnNpc(getNpcConfig(fn), i++);
            }
        }
    }

    private void spawnNpc(Config npcC, int ftId) {
        Position pos = getPos(npcC.getString("pos")).add(0.5, 0, 0.5);

        CompoundTag nbt = Entity.getDefaultNBT(pos);
        nbt.putInt("npcId", npcC.getInt("npcId"));

        Npc npc = new Npc(pos.getChunk(), nbt, ftId);
        npc.setCmd(npcC.getStringList("cmd"));
        npc.setMsg(npcC.getString("msg"));
        npc.setGn(npcC.getString("game"));
        npc.setNpcN(npcC.getString("npcName"));
        npc.spawnToAll();
    }

    private boolean delNpc(String npcN) {
        File f = new File(getDataFolder() + "/" + npcN + ".yml");
        return f.delete();
    }

    private boolean isOpCmd(String arg) {
        for (String cmd : new String[]{"cre", "del", "reload"}) {
            if (arg.equalsIgnoreCase(cmd))
                return true;
        }
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String text = "";
        if (sender instanceof Player) {

            switch (command.getLabel()) {
                case "play": // play <GameName>
                    if (args.length == 1) {
                        if (!GameAPI.quickJoin((Player) sender, args[0])) {
                            text = "Game: " + args[0] + "未被加载！无法快速进入！";
                        }
                    }
                    break;
                case "gnpc": // gnpc <args> <args>
                    if (args.length == 0) {

                        if (sender.isOp()) {
                            StringJoiner sj = new StringJoiner("\n- ", "GameNPC Help List", "");
                            sj.add("")
                                    .add("/gnpc <args> - 主要指令")
                                    .add("")
                                    .add("args:")
                                    .add("cre <npcName> <gameName(选填)> - 脚下位置创建一个NPC")
                                    .add("del <npcName> - 删除一个NPC")
                                    .add("reload - 重载NPC")
                                    .add("")
                                    .add("GameNPC By Nissining");
                            sender.sendMessage(sj.toString());
                        }

                    } else {

                        if (isOpCmd(args[0]) && !sender.isOp()) {
                            text = "权限不足！";

                        } else {

                            switch (args[0]) {
                                case "cre":
                                    createNpc(
                                            (Position) sender,
                                            args[1],
                                            args.length == 3 ? args[2] : ""
                                    );
                                    text = "已创建Npc" + args[1];
                                    break;
                                case "del":
                                    if (delNpc(args[1]))
                                        text = "删除成功";
                                    break;
                                case "reload":
                                    for (Entity en : ((Player) sender).level.getEntities()) {
                                        if (en instanceof Npc) {
                                            en.kill();
                                            en.close();
                                        }
                                    }
                                    spawnAllNpc();
                                    text = "已重载所有Npc";
                                    break;
                            }
                        }

                    }
                    break;
            }

            if (!text.isEmpty())
                sender.sendMessage(text);
        }
        return true;
    }

    @EventHandler
    public void onUnloadChunk(ChunkUnloadEvent event) {
        for (Entity en : event.getChunk().getEntities().values())
            if (en instanceof Npc) {
                event.setCancelled();
                break;
            }
    }

}

