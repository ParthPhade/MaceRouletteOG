package dev.swaggerstudio.macerouletteog.arena;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import java.util.ArrayDeque;
import java.util.Deque;

public class BlockRollbackTracker {

    private final Deque<BlockState> modifiedBlocks = new ArrayDeque<>();

    public void track(Block block) {
        // Only track the original state once per block location
        modifiedBlocks.push(block.getState());
    }

    public void rollback() {
        while (!modifiedBlocks.isEmpty()) {
            BlockState state = modifiedBlocks.pop();
            state.update(true, false); // true = force update, false = don't physics-trigger immediately
        }
    }

    public void clear() {
        modifiedBlocks.clear();
    }
}