/**
 * This file is part of Oracle, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Helion3 http://helion3.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.helion3.oracle.players;

import java.util.UUID;

import org.spongepowered.api.entity.player.Player;

public class PluginPlayer {
    private int playerId;
    private String player;
    private UUID playerUuid;

    /**
     *
     * @param playerId
     * @param player
     */
    public PluginPlayer( int playerId, Player player ){
        this( playerId, player.getUniqueId(), player.getName() );
    }

    /**
     *
     * @param playerId
     * @param playerUuid
     * @param player
     */
    public PluginPlayer( int playerId, UUID playerUuid, String player ){
        this.playerId = playerId;
        this.playerUuid = playerUuid;
        this.player = player;
    }

    /**
     *
     * @return
     */
    public void setId( int newId ){
        if( playerId > 0 ) throw new IllegalArgumentException("Cannot overwrite PrismPlayer primary key.");
        playerId = newId;
    }

    /**
     *
     * @return
     */
    public int getId(){
        return playerId;
    }

    /**
     *
     */
    public String getName(){
        return player;
    }

    /**
     *
     */
    public void setName( String name ){
        player = name;
    }

    /**
     *
     */
    public UUID getUUID(){
        return playerUuid;
    }

    /**
     *
     */
    public void setUUID( UUID uuid ){
        playerUuid = uuid;
    }
}