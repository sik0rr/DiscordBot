package com.sik0r.discordbot.utils;

import com.sik0r.discordbot.music.PlayerManager;
import com.sik0r.discordbot.music.TrackScheduler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.List;

public class BotUtils {

    public static boolean voiceChannelStatusCheck(SlashCommandInteractionEvent event, EmbedBuilder builder,
                                                  PlayerManager playerManager) {
        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();
        String nickname = event.getMember().getUser().getAsMention();
        if (!memberVoiceState.inAudioChannel()) {
            builder.setTitle(nickname + ", сначала зайди в голосовой канал!");
            event.replyEmbeds(builder.build()).queue();
            return false;
        }

        Member self = event.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inAudioChannel()) {
            event.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
        } else if (selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
            TrackScheduler scheduler = playerManager.getGuildMusicManager(event.getGuild()).getTrackScheduler();
            if (!scheduler.isPlaying()) {
                playerManager.clearQueue(event.getGuild());
                event.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
            } else {
                builder.setTitle(nickname + ", я уже занят в другом канале!");
                event.replyEmbeds(builder.build()).queue();
                return false;
            }
        }
        return true;
    }

    public static void addOrRemoveRole(GenericMessageReactionEvent event) {
        Member member = event.getMember();
        List<Role> memberRoles = member.getRoles();
        Guild guild = event.getGuild();
        String messageId = event.getMessageId();
        event.getChannel().retrieveMessageById(messageId).queue(message -> {
            Role role = message.getMentions().getRoles().get(0);
            if (event instanceof MessageReactionAddEvent) {
                if (!memberRoles.contains(role) && event.getReaction().getEmoji().asUnicode().getAsCodepoints().equals("U+1f44d")) {
                    guild.addRoleToMember(member, guild.getRoleById(role.getId())).queue();
                }
            } else {
                if (memberRoles.contains(role) && event.getReaction().getEmoji().asUnicode().getAsCodepoints().equals("U+1f44d")) {
                    guild.removeRoleFromMember(member, guild.getRoleById(role.getId())).queue();
                }
            }
        });
    }
}
