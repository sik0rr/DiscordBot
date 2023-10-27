package com.sik0r.discordbot;

import com.sik0r.discordbot.commands.misc.Shutdown;
import com.sik0r.discordbot.commands.musical.*;
import com.sik0r.discordbot.management.GuildJoinListener;
import com.sik0r.discordbot.commands.CommandManager;
import com.sik0r.discordbot.management.RoleManagement;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import java.util.Arrays;

public class Bot {

    private static final GatewayIntent[] gatewayIntents = {GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS};

    public static void main(String[] args) throws InterruptedException {

        CommandManager integration = new CommandManager();
        integration.addCommand(new Play());
        integration.addCommand(new Skip());
        integration.addCommand(new Pause());
        integration.addCommand(new Shutdown());
        integration.addCommand(new Queue());
        integration.addCommand(new ClearQueue());
        integration.addCommand(new Shuffle());
        integration.addCommand(new Repeat());

        JDA bot = JDABuilder.createDefault(System.getenv("TOKEN"))
                .addEventListeners(new GuildJoinListener(), new RoleManagement(), integration)
                .enableIntents(Arrays.asList(gatewayIntents))
                .build().awaitReady();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> bot.shutdown()));
    }
}
