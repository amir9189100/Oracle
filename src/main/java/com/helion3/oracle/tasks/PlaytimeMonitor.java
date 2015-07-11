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
package com.helion3.oracle.tasks;

import org.spongepowered.api.entity.player.Player;

import com.helion3.oracle.Oracle;
import com.helion3.oracle.events.OraclePlaytimeMilestoneEvent;
import com.helion3.oracle.utils.Playtime;
import com.helion3.oracle.utils.PlaytimeUtil;

public class PlaytimeMonitor implements Runnable {

	/**
	 *
	 */
	@Override
    public void run() {
		for (Player p : Oracle.getGame().getServer().getOnlinePlayers()) {
			Playtime playtime;
            try {
                playtime = PlaytimeUtil.getPlaytime(p);

                if( !Oracle.playtimeHours.containsKey(p) ) continue;

                int lastHourCount = Oracle.playtimeHours.get(p);

                if( playtime.getHours() > lastHourCount ){

                    Oracle.playtimeHours.put( p, playtime.getHours() );

                    Oracle.log("Throwing playtime hour increase event for " + p.getName());

                    // Throw event as this is a new player
                    OraclePlaytimeMilestoneEvent event = new OraclePlaytimeMilestoneEvent(p, playtime.getHours());
                    Bukkit.getServer().getPluginManager().callEvent( event );

                }

            } catch ( Exception e ) {
                e.printStackTrace();
                continue;
            }
		}
	}
}