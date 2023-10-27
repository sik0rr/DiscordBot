package com.sik0r.discordbot.commands.musical;

import com.sik0r.discordbot.music.PlayerManager;
import com.sik0r.discordbot.commands.ICommand;
import com.sik0r.discordbot.utils.BotUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Play implements ICommand {
    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Чтобы включить музло";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "track",
                "Название песни/ссылка на трек", true));
        options.add(new OptionData(OptionType.BOOLEAN, "shuffle",
                "Перемешать плейлист", false));
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        String name = event.getOption("track").getAsString();
        PlayerManager playerManager = PlayerManager.get();
        EmbedBuilder builder = new EmbedBuilder();
        if (!BotUtils.voiceChannelStatusCheck(event, builder, playerManager)) {
            return;
        }
        try {
            new URI(name);
        } catch (URISyntaxException e) {
            name = "ytsearch:" + name;
            playerManager.playByName(event.getGuild(), name);
            builder.setTitle("Плеер");
            builder.setDescription("Добавлено в очередь: " + name.substring(9));
            event.replyEmbeds(builder.build()).queue();
            return;
        }

        try {
            if (event.getOption("shuffle").getAsBoolean()) {
                playerManager.playWithShuffle(event.getGuild(), name);
            }
        } catch (NullPointerException e) {
            playerManager.play(event.getGuild(), name);
        }
        builder.setTitle("Плеер");
        builder.setDescription("Добавлено в очередь: " + name);
        event.replyEmbeds(builder.build()).queue();
    }
}
