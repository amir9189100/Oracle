# Oracle

*The Oracle* is a bukkit-based plugin with a wide range of administrative information-management - including detailed playtime tracking, IP tracking, BungeeCord support, cross-server ban/unban management, player warning system, and more!

Oracle is based on dhmcStats, a custom/private plugin that formerly ran DHMC.us. Most of that functionality has been rewritten and polished for release as Oracle.


## Features Overview

- Detailed **playtime tracking**: Tracks join/quit timestamps, IPs, with checks for uncaught disconnects, and more.
- Supports servers behind **BungeeCord**
- Use `/played` to view the total playtime for a player
- `/seen` command for viewing first join and most recent playtime
- Easily check if a player `/ison` 
- **Cross-server ban/unban** - Full record of previous bans
- Player **warning system**. `/warn` players for rule violation. They're shown a highlighted message of the warning, and staff are alerted if a player gets more than three.
- Log commands and world/coordinates of their use to console (turn off if using Prism)
- Scheduled broadcast announcements (in-dev)
- New-player **guide book** - Give new players a guide book with rules and helpful info for your server
- Attempt to kick Minechat users if configured.

(Coming soon: Better reports, stats, player history, in-game way to control announcements)


## Installation

Throw into `plugins` directory. Start server once to generate config.

This plugin *requires* mysql, so you'll need to configure the connection settings in the config.


## Configuration

Most of the configuration nodes are self-explanatory - with mysql connection properties, debug mode, and enabling/disabling specific features of Oracle.

Some clarifications:

- Disabling a feature will turn off any feature that relies on it. i.e. turning off join tracking turns of alt ip warnings, etc.
- `oracle.joins.use-bungeecord` should be true if you use a BungeeCord proxy, otherwise every IP you record will be incorrect.
- The guidebook is a book given to a players on first join (if enabled) and has the title/author/contents configured. Each sub-node to the `content` is a different page of the book. Use "\n" character to make new lines in the content.


## Notes

Oracle does not have any type of AFK (away-from-keyboard) features and will not (yet) pause tracking if a user is AFK. Please use a plugin like `Essentials` to track if players are AFK - it's highly recommended you set it to auto-kick after a length of time AFK.

Minechat logins are treated by bukkit as if the actual player has logged in, and playtime will be tracked. We can attempt to detect Minechat and kick the player, but it can be overridden and for now, isn't 100% effective.


## Permissions / Commands

*Ban-related*

- `oracle.ban` - `/ban [player] [reason]` - Ban a player.
- `oracle.unban` - `/unban [player]` - Unban a player.
- `oracle.lookup` - `/lookup [player]` - Check if a player has been banned.

*Joins-related*

- `oracle.alts` - `/alts [player]` - View all alternate possible accounts for this player
- `oracle.alerts.alts` - Alerts player with this permission when a first-time user joins who has alts
- `oracle.minechat` - Allows player to bypass the Minechat blocker, if enabled.
- `oracle.seen` - `/seen [player]` - View the date a player joined and was last here
- `oracle.played` - `/played [player]` - View the total playtime of a player.

*Warnings-related*

- `oracle.warn` - `/warn [player] [message]` - File a warning for a player
- `oracle.warnings` - `/warnings [player]` - View warnings for a player

*Other*

- `oracle.ison` - `/ison [player]` - Check if a player is online. Supports partial usernames.
- `oracle.reload` - `/oracle reload` - Reloads the config

*Commands that only accept a single [player] argument may be left blank, and will default to the current user.*

## Events & API
 
Oracle contains a ton of useful information, so we'll work on evolving these events and the API over time.
 
- `OracleFirstTimePlayerEvent` - Fired the first time a player joins with a `Player getPlayer()` method.

There's a basic internal API you can use by including the plugin in your project.


## Get Help

IRC: irc.esper.net #prism

[Bug tracker](https://snowy-evening.com/botsko/oracle)   

[Source](https://github.com/prism/Oracle)    
           
## Credits

This plugin was custom designed by viveleroi for the amazing *s.dhmc.us* Minecraft server.


## Authors

- viveleroi (Creator, Lead Developer)
- nasonfish (Contributor to dhmcStats)

### Metrics

This plugin utilizes Hidendra's plugin metrics system, which, if enabled, anonymously tracks the following about your server at mcstats.org: A unique identifier, server java version, online mode, plugin & server versions, OS version/name and architecture, cpu core count, player counts. 

This information is used purely to help inform our technical decisions and boost our egos with how many people are enjoying our work.

Opting out of this service can be done by editing plugins/Plugin Metrics/config.yml and changing opt-out to true.
