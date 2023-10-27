package com.sik0r.discordbot.commands.musical;

import com.sik0r.discordbot.music.PlayerManager;
import com.sik0r.discordbot.commands.ICommand;
import com.sik0r.discordbot.utils.BotUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;

public class Pause implements ICommand {

    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public String getDescription() {
        return "Чтобы запаузить/распаузить музло";
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.emptyList();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        PlayerManager playerManager = PlayerManager.get();
        if (!BotUtils.voiceChannelStatusCheck(event, builder, playerManager)) {
            return;
        }

        Member self = event.getGuild().getSelfMember();
        boolean isPaused = playerManager.getGuildMusicManager(event.getGuild())
                            .getTrackScheduler().getAudioPlayer().isPaused();
        playerManager.getGuildMusicManager(event.getGuild()).getTrackScheduler().getAudioPlayer().setPaused(!isPaused);
        builder.setTitle("Пауза");
        String currentTrack = playerManager.getGuildMusicManager(event.getGuild())
                .getTrackScheduler().getAudioPlayer().getPlayingTrack().getInfo().title;
        builder.setDescription(!isPaused ? "Трек " + currentTrack + " приостановлен" : "Трек " + currentTrack + " возобновлён");
        event.replyEmbeds(builder.build()).queue();
    }
}
