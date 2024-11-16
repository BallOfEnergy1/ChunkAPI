package com.falsepattern.chunk.internal.mixin.mixins.common.vanilla;

import com.falsepattern.chunk.internal.BlockPosUtil;
import com.falsepattern.chunk.internal.DataRegistryImpl;
import com.falsepattern.chunk.internal.impl.CustomPacketBlockChange;
import lombok.SneakyThrows;
import lombok.val;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.io.IOException;

@Mixin(S23PacketBlockChange.class)
public abstract class S23PacketBlockChangeMixin implements CustomPacketBlockChange {
    @Shadow(aliases = "field_148887_a")
    private int xCoord;
    @Shadow(aliases = "field_148885_b")
    private int yCoord;
    @Shadow(aliases = "field_148886_c")
    private int zCoord;

    @Shadow public Block field_148883_d;

    @Shadow public int field_148884_e;

    @Override
    public void chunkapi$init(int x, int y, int z, Chunk chunk) {
        xCoord = x;
        yCoord = y;
        zCoord = z;
        field_148883_d = chunk.getBlock(x, y, z);
        field_148884_e = chunk.getBlockMetadata(x, y, z);
        DataRegistryImpl.writeBlockToPacket(chunk, x, y, z, (S23PacketBlockChange) (Object) this);
    }

    @Inject(method = "<init>(IIILnet/minecraft/world/World;)V",
            at = @At("RETURN"),
            require = 1)
    private void writeBlockToPacket(int x, int y, int z, World world, CallbackInfo ci) {
        val chunk = world.getChunkFromBlockCoords(x, z);
        DataRegistryImpl.writeBlockToPacket(chunk, x & 0xf, y, z & 0xf, (S23PacketBlockChange) (Object) this);
    }

    /**
     * @author FalsePattern
     * @reason Integrate
     */
    @Overwrite
    public void readPacketData(PacketBuffer data) throws IOException {
        long packed = data.readLong();
        xCoord = BlockPosUtil.getX(packed);
        yCoord = BlockPosUtil.getY(packed);
        zCoord = BlockPosUtil.getZ(packed);
        DataRegistryImpl.readBlockPacketFromBuffer((S23PacketBlockChange) (Object) this, data);
    }

    /**
     * @author FalsePattern
     * @reason Integrate
     */
    @Overwrite
    public void writePacketData(PacketBuffer data) throws IOException {
        long packed = BlockPosUtil.packToLong(xCoord, yCoord, zCoord);
        data.writeLong(packed);
        DataRegistryImpl.writeBlockPacketToBuffer((S23PacketBlockChange) (Object) this, data);
    }

    @Override
    public void chunkapi$x(int value) {
        xCoord = value;
    }

    @Override
    public void chunkapi$y(int value) {
        yCoord = value;
    }

    @Override
    public void chunkapi$z(int value) {
        zCoord = value;
    }

    @Override
    public int chunkapi$x() {
        return xCoord;
    }

    @Override
    public int chunkapi$y() {
        return yCoord;
    }

    @Override
    public int chunkapi$z() {
        return zCoord;
    }
}
