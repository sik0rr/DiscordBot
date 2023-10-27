package com.sik0r.discordbot.commands.musical;

import com.sik0r.discordbot.music.PlayerManager;
import com.sik0r.discordbot.commands.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;

public class Skip implements ICommand {
    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getDescription() {
        return "Чтобы скипнуть музло";
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.emptyList();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();
        EmbedBuilder builder = new EmbedBuilder();
        String nickname = event.getMember().getNickname();
        if (!memberVoiceState.inAudioChannel()) {
            builder.setTitle(nickname + ", сначала зайди в голосовой канал!");
            event.replyEmbeds(builder.build()).queue();
            return;
        }

        Member self = event.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inAudioChannel()) {
            event.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
        } else if (selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
            builder.setTitle(nickname + ", я уже занят в другом канале!");
            event.replyEmbeds(builder.build()).queue();
            return;
        }

        PlayerManager playerManager = PlayerManager.get();
        playerManager.skip(event.getGuild());

        builder.setTitle("Пропуск");
        builder.setDescription("Трек пропущен");
        event.replyEmbeds(builder.build()).queue();
    }
}
