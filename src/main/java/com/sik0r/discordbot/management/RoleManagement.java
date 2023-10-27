package com.sik0r.discordbot.management;
import com.sik0r.discordbot.utils.BotUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class RoleManagement extends ListenerAdapter {

    String roleChannelId = System.getenv("ROLE_CHANNEL");
    List<String> mentionedRoles = new ArrayList<>();

    @Override
    public void onReady(ReadyEvent event) {
        Guild guild = event.getJDA().getGuilds().get(0);
        TextChannel roleChannel = guild.getTextChannelById(roleChannelId);
        List<String> reactionRoles = List.of(System.getenv("REACTION_ROLES").split(","));
        List<Message> messages = roleChannel.getHistory().retrievePast(100).complete();
        for (Message message : messages) {
            List<Role> mentionedRole = message.getMentions().getRoles();
            if (mentionedRole.isEmpty()) {
                continue;
            }
            mentionedRoles.add(mentionedRole.get(0).getId());
        }
        if (mentionedRoles.isEmpty()) {
            for (String role : reactionRoles) {
                sendMessageWithRole(role, roleChannel, guild);
            }
            return;
        }
        for (String role : reactionRoles) {
            if (!mentionedRoles.contains(role)) {
                sendMessageWithRole(role, roleChannel, guild);
            }
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getChannel().getId().equals(roleChannelId)) {
            BotUtils.addOrRemoveRole(event);
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if (event.getChannel().getId().equals(roleChannelId)) {
            BotUtils.addOrRemoveRole(event);
        }
    }

    private void sendMessageWithRole(String role, TextChannel roleChannel, Guild guild) {
        roleChannel.sendMessage(
                String.format("Чтобы получить роль %s, поставь палец вверх.",
                        guild.getRoleById(role).getAsMention())).queue();
    }
}
