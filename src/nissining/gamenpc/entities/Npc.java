package nissining.gamenpc.entities;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.TextFormat;
import nissining.gamenpc.GameNPC;
import nissining.gamenpc.gameUtils.GameAPI;
import nissining.gamenpc.floatingTextPacket.FloatingTextAPI;

import java.util.ArrayList;
import java.util.List;

public class Npc extends EntityLiving {

    private String gn;
    private String npcN;
    private List<String> cmd = new ArrayList<>();
    private String msg;

    private int refTime = 0; // 40 tick更新一次
    private int hideTime = 0;
    private final List<String> hide = new ArrayList<>();

    private final int ftId;

    public Npc(FullChunk chunk, CompoundTag nbt, int ftId) {
        super(chunk, nbt);
        this.ftId = ftId;
    }

    @Override
    public int getNetworkId() {
        if (this.namedTag.contains("npcId")) {
            return this.namedTag.getInt("npcId");
        }
        return 38;
    }

    @Override
    public float getHeight() {
        return 2.8F;
    }

    @Override
    protected float getGravity() {
        return 0F;
    }

    @Override
    public float getWidth() {
        return 0.4F;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        lookAt(this.level.getSafeSpawn());
        setNameTagAlwaysVisible(false);
    }

    public void setGn(String gn) {
        this.gn = gn;
    }

    public void setCmd(List<String> cmd) {
        this.cmd = cmd;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setNpcN(String npcN) {
        this.npcN = npcN;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (refTime++ >= 40) {
            refTime = 0;

            String gameStatus = npcN;

            List<String> gameData = GameAPI.getGameData(gn);
            if (gameData.size() == 2) {
                gameStatus = "§aHit to join!!@n@n" + gameData.get(0) + "@n§e§l" + gameData.get(1) + "名玩家在游戏";
            }

            FloatingTextAPI.cleanFloatingTextById(this.level, this.ftId);
            FloatingTextAPI.spawnFloatingText(this.add(0, getEyeHeight() + 1), this.ftId, gameStatus);
        }

        // hide player
        if (hideTime++ % 10 == 0 && GameNPC.isHide) {
            hideTime = 0;
            for (Player player : level.getPlayers().values()) {
                if (player.distance(this) < 3.0d) {
                    if (!hide.contains(player.getName())) {
                        hide.add(player.getName());
                        player.addEffect(
                                Effect.getEffect(Effect.INVISIBILITY)
                                        .setVisible(false)
                                        .setAmplifier(0)
                                        .setDuration(9 * 99999)
                        );
                        player.sendMessage(TextFormat.YELLOW + "进入隐身状态！");
                    }
                } else {
                    if (hide.contains(player.getName())) {
                        hide.remove(player.getName());
                        player.removeEffect(Effect.INVISIBILITY);
                        player.sendMessage(TextFormat.YELLOW + "退出隐身状态");
                    }
                }
            }
        }

        return super.onUpdate(currentTick);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        source.setCancelled();
        if (source instanceof EntityDamageByEntityEvent) {
            Entity p1 = ((EntityDamageByEntityEvent) source).getDamager();
            if (p1 instanceof Player) {
                // 指令
                cmd.forEach(cmd -> getServer().dispatchCommand((Player) p1, cmd));
                // 消息
                if (!msg.trim().equalsIgnoreCase("未修改"))
                    ((Player) p1).sendMessage(msg);
                // 加入指定游戏
                if (!GameAPI.quickJoin((Player) p1, gn)) {
                    ((Player) p1).sendMessage("Game: " + gn + " not Loaded!");
                }
            }
        }
        return super.attack(source);
    }

    @Override
    public void saveNBT() {

    }

    private void lookAt(Position position) {
        double x = this.x - position.x;
        double y = this.y - position.y;
        double z = this.z - position.z;
        double yaw = Math.asin(x / Math.sqrt(x * x + z * z)) / Math.PI * 180.0D;
        double asin = Math.asin(y / Math.sqrt(x * x + z * z + y * y)) / Math.PI * 180.0D;
        long pitch = Math.round(asin);
        if (z > 0.0D) yaw = -yaw + 180.0D;
        setRotation(yaw, (float) pitch);
    }

}
