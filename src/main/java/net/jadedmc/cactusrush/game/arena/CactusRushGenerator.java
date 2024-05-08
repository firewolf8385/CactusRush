/*
 * This file is part of CactusRush, licensed under the MIT License.
 *
 *  Copyright (c) JadedMC
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.cactusrush.game.arena;

import net.jadedmc.jadedcore.worlds.generators.JadedChunkGenerator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class CactusRushGenerator extends JadedChunkGenerator {

    /**
     * Creates the Chunk Generator.
     */
    public CactusRushGenerator() {
        super("cactusrush");
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int i, int i1, BiomeGrid biomeGrid) {
        // Loops through each block setting the biome to PLAINS.
        for (int blockX = 0; blockX < 16; blockX++) {
            for (int blockZ = 0; blockZ < 16; blockZ++) {
                biomeGrid.setBiome(blockX, blockZ, Biome.PLAINS);
            }
        }

        // Creates a layer of coal blocks at y: 55.
        ChunkData chunkData = createChunkData(world);
        chunkData.setRegion(0, 55, 0, 16, 56, 16, Material.COAL_BLOCK);

        return chunkData;
    }

    /**
     * Sets the world spawn location.
     * @param world World to set spawn location of.
     * @param random Random number generator (unused).
     * @return Spawn location.
     */
    @Override
    public Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
        return new Location(world, 0, 66, 0);
    }
}