package nissining.gamenpc.floatingTextPacket;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 *
 * Java Version FloatingTextAPI By Nissining
 *
 * 2022/9/26
 *
 */
public class FloatingTextAPI {

    public static void spawnFloatingText(Position pos, int id, String text) {
        String[] ss = text.split("@n");

        double height = 0.0d;
        for (int i = ss.length - 1; i > -1; i--) {
            CompoundTag nbt = Entity.getDefaultNBT(pos.add(0, height += 0.3d));
            Floating floating = new Floating(pos.getChunk(), nbt, ss[i].replace("_", " "), id);
            floating.spawnToAll();
        }

    }

    public static void cleanAllFloatingText(Level level) {
        for (Entity entity : level.getEntities()) {
            if (entity instanceof Floating) {
                entity.kill();
                entity.close();
            }
        }
    }

    public static void cleanFloatingTextById(Level level, int id) {
        for (Entity entity : level.getEntities()) {
            if (entity instanceof Floating && ((Floating) entity).getFtId() == id) {
                entity.kill();
                entity.close();
            }
        }
    }

}
