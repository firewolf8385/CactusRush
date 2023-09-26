/*
 * This file is part of Cactus Rush, licensed under the MIT License.
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
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.cactusrush.utils;

import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Collection;
import java.util.HashSet;

/**
 * A collection of utility methods that work with blocks.
 */
public class BlockUtils {

    /**
     * Gets the equivalent block of a given block, but in a different world.
     * @param world World to get new blocks in.
     * @param blocks Collection of blocks to get.
     * @return Collection of blocks in the new world.
     */
    public static Collection<Block> replaceWorld(World world, Collection<Block> blocks) {
        Collection<Block> newBlocks = new HashSet<>();

        for(Block block : blocks) {
            Block newBlock = world.getBlockAt(block.getX(), block.getY(), block.getZ());
            newBlocks.add(newBlock);
        }

        return newBlocks;
    }

}
