/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  net.minecraft.server.v1_7_R4.Block
 *  net.minecraft.server.v1_7_R4.BlockContainer
 *  net.minecraft.server.v1_7_R4.Blocks
 *  net.minecraft.server.v1_7_R4.Chunk
 *  net.minecraft.server.v1_7_R4.ChunkCoordIntPair
 *  net.minecraft.server.v1_7_R4.ChunkSection
 *  net.minecraft.server.v1_7_R4.IContainer
 *  net.minecraft.server.v1_7_R4.TileEntity
 *  net.minecraft.server.v1_7_R4.WorldServer
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.craftbukkit.v1_7_R4.CraftWorld
 *  org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer
 *  org.bukkit.entity.Player
 */
package net.frozenorb.qlib.util;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.server.v1_7_R4.Block;
import net.minecraft.server.v1_7_R4.BlockContainer;
import net.minecraft.server.v1_7_R4.Blocks;
import net.minecraft.server.v1_7_R4.Chunk;
import net.minecraft.server.v1_7_R4.ChunkCoordIntPair;
import net.minecraft.server.v1_7_R4.ChunkSection;
import net.minecraft.server.v1_7_R4.IContainer;
import net.minecraft.server.v1_7_R4.TileEntity;
import net.minecraft.server.v1_7_R4.WorldServer;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class BlockUtils {
    private static final Set<Material> INTERACTABLE = ImmutableSet.of((Object)Material.FENCE_GATE, (Object)Material.FURNACE, (Object)Material.BURNING_FURNACE, (Object)Material.BREWING_STAND, (Object)Material.CHEST, (Object)Material.HOPPER, (Object[])new Material[]{Material.DISPENSER, Material.WOODEN_DOOR, Material.STONE_BUTTON, Material.WOOD_BUTTON, Material.TRAPPED_CHEST, Material.TRAP_DOOR, Material.LEVER, Material.DROPPER, Material.ENCHANTMENT_TABLE, Material.BED_BLOCK, Material.ANVIL, Material.BEACON});

    public static boolean isInteractable(org.bukkit.block.Block block) {
        return BlockUtils.isInteractable(block.getType());
    }

    public static boolean isInteractable(Material material) {
        return INTERACTABLE.contains((Object)material);
    }

    public static boolean setBlockFast(World world, int x, int y, int z, int blockId, byte data) {
        WorldServer w = ((CraftWorld)world).getHandle();
        Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
        return BlockUtils.a(chunk, x & 0xF, y, z & 0xF, Block.getById((int)blockId), data);
    }

    private static void queueChunkForUpdate(Player player, int cx, int cz) {
        ((CraftPlayer)player).getHandle().chunkCoordIntPairQueue.add(new ChunkCoordIntPair(cx, cz));
    }

    private static boolean a(Chunk that, int i, int j, int k, Block block, int l) {
        TileEntity tileentity;
        int i1 = k << 4 | i;
        if (j >= that.b[i1] - 1) {
            that.b[i1] = -999;
        }
        int j1 = that.heightMap[i1];
        Block block1 = that.getType(i, j, k);
        int k1 = that.getData(i, j, k);
        if (block1 == block && k1 == l) {
            return false;
        }
        boolean flag = false;
        ChunkSection chunksection = that.getSections()[j >> 4];
        if (chunksection == null) {
            if (block == Blocks.AIR) {
                return false;
            }
            ChunkSection chunkSection = new ChunkSection(j >> 4 << 4, !that.world.worldProvider.g);
            that.getSections()[j >> 4] = chunkSection;
            chunksection = chunkSection;
            flag = j >= j1;
        }
        int l1 = that.locX * 16 + i;
        int i2 = that.locZ * 16 + k;
        if (!that.world.isStatic) {
            block1.f(that.world, l1, j, i2, k1);
        }
        if (!(block1 instanceof IContainer)) {
            chunksection.setTypeId(i, j & 0xF, k, block);
        }
        if (!that.world.isStatic) {
            block1.remove(that.world, l1, j, i2, block1, k1);
        } else if (block1 instanceof IContainer && block1 != block) {
            that.world.p(l1, j, i2);
        }
        if (block1 instanceof IContainer) {
            chunksection.setTypeId(i, j & 0xF, k, block);
        }
        if (chunksection.getTypeId(i, j & 0xF, k) != block) {
            return false;
        }
        chunksection.setData(i, j & 0xF, k, l);
        if (flag) {
            that.initLighting();
        }
        if (block1 instanceof IContainer && (tileentity = that.e(i, j, k)) != null) {
            tileentity.u();
        }
        if (!(that.world.isStatic || that.world.captureBlockStates && !(block instanceof BlockContainer))) {
            block.onPlace(that.world, l1, j, i2);
        }
        if (block instanceof IContainer) {
            if (that.getType(i, j, k) != block) {
                return false;
            }
            tileentity = that.e(i, j, k);
            if (tileentity == null) {
                tileentity = ((IContainer)block).a(that.world, l);
                that.world.setTileEntity(l1, j, i2, tileentity);
            }
            if (tileentity != null) {
                tileentity.u();
            }
        }
        that.n = true;
        return true;
    }
}

