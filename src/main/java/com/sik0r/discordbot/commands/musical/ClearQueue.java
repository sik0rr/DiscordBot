package com.sik0r.discordbot.commands.musical;

import com.sik0r.discordbot.music.PlayerManager;
import com.sik0r.discordbot.commands.ICommand;
import com.sik0r.discordbot.utils.BotUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;

public class ClearQueue implements ICommand {
    @Override
    public String getName() {
        return "clearqueue";
    }

    @Override
    public String getDescription() {
        return "Этническая чистка плейлиста";
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

        playerManager.clearQueue(event.getGuild());
        builder.setTitle("Очищение плейлиста");
        builder.setDescription("Плейлист очищен");
        event.replyEmbeds(builder.build()).queue();
    }
}
