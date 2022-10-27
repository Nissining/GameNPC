package nissining.gamenpc.floatingTextPacket;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.projectile.EntitySnowball;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class Floating extends EntityLiving {

    private final int ftId;

    @Override
    public int getNetworkId() {
        return EntitySnowball.NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0f;
    }

    @Override
    public float getHeight() {
        return 0f;
    }

    @Override
    protected float getGravity() {
        return 0f;
    }

    public Floating(FullChunk chunk, CompoundTag nbt, String string, int id) {
        super(chunk, nbt);
        setNameTag(string);
        setNameTagAlwaysVisible(true);
        setScale(0.01f);
        this.ftId = id;
    }

    public int getFtId() {
        return ftId;
    }

    @Override
    public void saveNBT() {
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        source.setCancelled();
        return super.attack(source);
    }

    @Override
    public boolean onUpdate(int i) {
        return false;
    }

    @Override
    public boolean mountEntity(Entity entity) {
        return false;
    }


}
