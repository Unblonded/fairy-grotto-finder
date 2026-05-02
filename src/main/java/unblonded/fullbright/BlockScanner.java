package unblonded.fullbright;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import unblonded.fullbright.util.Color;
import unblonded.fullbright.util.PosColor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class BlockScanner {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(
            Math.max(1, Runtime.getRuntime().availableProcessors() / 2),
            r -> {
                Thread t = new Thread(r, "fullbright-scanner");
                t.setDaemon(true);
                return t;
            }
    );

    public static final List<BlockPos> logs = new CopyOnWriteArrayList<>();

    public static ScanTask scan(String blockId, Color color, int radiusChunks) {
        return new ScanTask(blockId, color, radiusChunks);
    }

    public static class ScanTask {
        private final String blockId;
        private final Color color;
        private final int radiusChunks;
        private final List<PosColor> results = new CopyOnWriteArrayList<>();
        private volatile boolean done = false;
        private volatile boolean cancelled = false;
        private Future<?> future;

        private ScanTask(String blockId, Color color, int radiusChunks) {
            this.blockId = blockId;
            this.color = color;
            this.radiusChunks = radiusChunks;
        }

        /** Kicks off the scan asynchronously and returns itself for chaining. */
        public ScanTask execute() {
            MinecraftClient mc = MinecraftClient.getInstance();
            BlockScanner.logs.clear();
            if (mc.player == null || mc.world == null) {
                done = true;
                return this;
            }

            // Resolve block on the main thread snapshot before going async
            Block target = Registries.BLOCK.get(Identifier.of(blockId));
            ChunkPos centerChunk = mc.player.getChunkPos();
            World world = mc.world;

            // Snapshot the chunk list — safe to read chunk positions off thread
            List<ChunkPos> chunksToScan = new ArrayList<>();
            for (int cx = centerChunk.x - radiusChunks; cx <= centerChunk.x + radiusChunks; cx++) {
                for (int cz = centerChunk.z - radiusChunks; cz <= centerChunk.z + radiusChunks; cz++) {
                    chunksToScan.add(new ChunkPos(cx, cz));
                }
            }

            future = EXECUTOR.submit(() -> {
                // Split chunks across available threads using invokeAll
                int threads = Math.max(1, Runtime.getRuntime().availableProcessors() / 2);
                int chunkSize = Math.max(1, (int) Math.ceil((double) chunksToScan.size() / threads));

                List<Callable<Void>> tasks = new ArrayList<>();
                for (int i = 0; i < chunksToScan.size(); i += chunkSize) {
                    List<ChunkPos> slice = chunksToScan.subList(i, Math.min(i + chunkSize, chunksToScan.size()));
                    tasks.add(() -> {
                        for (ChunkPos cp : slice) {
                            if (cancelled) return null;

                            // getChunk is safe to call off-thread for already-loaded chunks
                            WorldChunk chunk = world.getChunkManager().getWorldChunk(cp.x, cp.z);
                            if (chunk == null) continue;

                            scanChunk(chunk, target, world);
                        }
                        return null;
                    });
                }

                try {
                    // Use a child pool so we don't deadlock the parent executor
                    ExecutorService childPool = Executors.newFixedThreadPool(threads);
                    childPool.invokeAll(tasks);
                    childPool.shutdown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done = true;
                }
            });

            return this;
        }

        private void scanChunk(WorldChunk chunk, Block target, World world) {
            int minY = world.getBottomY();
            int maxY = world.getTopYInclusive();

            int baseX = chunk.getPos().getStartX();
            int baseZ = chunk.getPos().getStartZ();

            for (int x = baseX; x < baseX + 16; x++) {
                for (int z = baseZ; z < baseZ + 16; z++) {
                    for (int y = minY; y <= maxY; y++) {
                        if (cancelled) return;
                        BlockPos pos = new BlockPos(x, y, z);
                        if (isExcluded(pos)) continue;
                        if (chunk.getBlockState(pos).getBlock() == target) {
                            PosColor found = new PosColor(pos, color);
                            results.add(found);
                            BlockScanner.logs.add(found.pos);
                        }
                    }
                }
            }
        }

        private boolean isExcluded(BlockPos pos) {
            return pos.getX() >= 500 && pos.getX() <= 524
                    && pos.getY() >= 100 && pos.getY() <= 125
                    && pos.getZ() >= 554 && pos.getZ() <= 562;
        }

        /** Blocks calling thread until scan completes, then returns results. */
        public List<PosColor> await() {
            try {
                if (future != null) future.get();
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
            }
            return results;
        }

        /** Non-blocking — returns whatever has been found so far. */
        public List<PosColor> results() {
            return results;
        }

        public boolean isDone() { return done; }

        public void cancel() {
            cancelled = true;
            if (future != null) future.cancel(true);
        }
    }
}