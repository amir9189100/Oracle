/**
 * This file is part of Prism, licensed under the MIT License (MIT).
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
package com.helion3.oracle;

import java.io.File;
import java.io.IOException;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class Configuration {

    private ConfigurationNode rootNode = null;

    /**
     * Loads (creates new if needed) Prism configuration file.
     * @param defaultConfig
     * @param configManager
     */
    public Configuration(File defaultConfig, ConfigurationLoader<CommentedConfigurationNode> configManager) {
        try {
            // If file does not exist, we must create it
            if (!defaultConfig.exists()) {
                defaultConfig.getParentFile().mkdirs();
                defaultConfig.createNewFile();
                rootNode = configManager.createEmptyNode(ConfigurationOptions.defaults());
                Oracle.getLogger().info("Creating new config at mods/Oracle/Oracle.conf");
            } else {
                rootNode = configManager.load();
            }

//            // Database
//            config.addDefault("oracle.database.max-pool-connections", 20);
//            config.addDefault("oracle.database.max-wait", 20000);
//

//
//            // Features
//            config.addDefault("oracle.bans.enabled", true);
//            config.addDefault("oracle.joins.use-bungeecord", false);
//            config.addDefault("oracle.warnings.enabled", true);
//
//            config.addDefault("oracle.guidebook.enabled", false);
//            config.addDefault("oracle.guidebook.author", "viveleroi");
//            config.addDefault("oracle.guidebook.title", "Server Guide");
//            config.addDefault("oracle.guidebook.contents", new String[]{"Welcome to the server!","Please read all rules at spawn."});
//
//            // Misc
//            config.addDefault("oracle.log-command-use-to-console", true);
//
//            config.addDefault("oracle.kick-minechat", true);

            // Database
            ConfigurationNode dbName = rootNode.getNode("db", "name");
            if (dbName.isVirtual()) {
                dbName.setValue("oracle");
            }

            ConfigurationNode dbTablePrefix = rootNode.getNode("db", "tablePrefix");
            if (dbTablePrefix.isVirtual()) {
                dbTablePrefix.setValue("oracle");
            }

            ConfigurationNode dbHost = rootNode.getNode("db", "host");
            if (dbHost.isVirtual()) {
                dbHost.setValue("127.0.0.1");
            }

            ConfigurationNode dbPort = rootNode.getNode("db", "port");
            if (dbPort.isVirtual()) {
                dbPort.setValue(3306);
            }

            ConfigurationNode dbUser = rootNode.getNode("db", "user");
            if (dbUser.isVirtual()) {
                dbUser.setValue("root");
            }

            ConfigurationNode dbPass = rootNode.getNode("db", "pass");
            if (dbPass.isVirtual()) {
                dbPass.setValue("");
            }

            // Config
            ConfigurationNode serverAlias = rootNode.getNode("server-alias");
            if (serverAlias.isVirtual()) {
                serverAlias.setValue("main");
            }

            // Features
            ConfigurationNode joinsEnabled = rootNode.getNode("joins", "enabled");
            if (joinsEnabled.isVirtual()) {
                joinsEnabled.setValue(true);
            }

            // Save
            try {
                configManager.save(rootNode);
            } catch(IOException e) {
                // @todo handle properly
                e.printStackTrace();
            }
        } catch (IOException e) {
            // @todo handle properly
            e.printStackTrace();
        }
    }

    /**
     * Shortcut to rootNode.getNode().
     *
     * @param path Object[] Paths to desired node
     * @return ConfigurationNode
     */
    public ConfigurationNode getNode(Object... path) {
        return rootNode.getNode(path);
    }
}
