package com.jdte.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;

import java.util.List;

/** Handles controller drops before the normal playerDestroy callback can be skipped by multipart removal. */
public final class ControllerDropHelper {
    private static final ThreadLocal<PendingDrop> PENDING = new ThreadLocal<>();

    private ControllerDropHelper() {
    }

    public static void clearPending() {
        PENDING.remove();
    }

    public static boolean hasPending(BlockPos pos) {
        PendingDrop pending = PENDING.get();
        return pending != null && pending.pos().equals(pos);
    }

    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.isCanceled() || !(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        BlockState state = event.getState();
        if (!isController(state)) {
            return;
        }
        Player player = event.getPlayer();
        if (player.isCreative()) {
            return;
        }
        BlockPos pos = event.getPos();
        dropFromPlayerBreak(state, level, pos, level.getBlockEntity(pos), player,
                player.getMainHandItem().copy());
    }

    public static void dropFromPlayerBreak(BlockState state, Level level, BlockPos pos,
                                           BlockEntity blockEntity, Player player, ItemStack tool) {
        if (level.isClientSide() || player.isCreative() || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        List<ItemStack> drops = Block.getDrops(state, serverLevel, pos, blockEntity, player, tool);
        if (drops.isEmpty()) {
            ItemStack fallback = new ItemStack(state.getBlock().asItem());
            if (!fallback.isEmpty()) {
                drops = List.of(fallback);
            }
        }
        for (ItemStack drop : drops) {
            if (!drop.isEmpty()) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), drop);
            }
        }
        PENDING.set(new PendingDrop(pos));
    }

    public static boolean consumePending(BlockPos pos) {
        PendingDrop pending = PENDING.get();
        PENDING.remove();
        return pending != null && pending.pos().equals(pos);
    }

    private record PendingDrop(BlockPos pos) {
    }

    private static boolean isController(BlockState state) {
        return state.getBlock() instanceof GreenhouseBlock
                || state.getBlock() instanceof LargeGreenhouseBlock
                || state.getBlock() instanceof LifeSynthesisVatBlock;
    }
}
