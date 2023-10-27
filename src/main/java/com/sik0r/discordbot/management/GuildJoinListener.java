package com.sik0r.discordbot.management;


import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GuildJoinListener extends ListenerAdapter {
    List<String> greetPhrases = Arrays.asList("Эйоу, %s", "Ого это же %s!", "%s! Явился, не запылился!");
    List<String> goodbyePhrases = Arrays.asList("%s, пока!", "%s сбежал!", "Ну и проваливай, %s!");

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        String role = System.getenv("DEFAULT_ROLE");
        if (role != null) {
            event.getGuild().addRoleToMember(event.getMember(),
                    event.getGuild().getRoleById(System.getenv("DEFAULT_ROLE"))).queue();
        }
        event.getGuild().getSystemChannel().sendMessage(
                String.format(greetPhrases.get(new Random().nextInt(greetPhrases.size())),
                        event.getUser().getName())).queue();
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        event.getGuild().getSystemChannel()
                .sendMessage(String.format(goodbyePhrases.get(new Random().nextInt(goodbyePhrases.size())),
                        event.getUser().getName())).queue();
    }
}
