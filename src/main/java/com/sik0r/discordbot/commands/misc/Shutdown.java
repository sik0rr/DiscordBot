package com.sik0r.discordbot.commands.misc;

import com.sik0r.discordbot.commands.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;

public class Shutdown implements ICommand {
    @Override
    public String getName() {
        return "shutdown";
    }

    @Override
    public String getDescription() {
        return "Ёбнуть бота";
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.emptyList();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getMember().getRoles().contains(event.getGuild().getRoleById("386569030367969280"))) {
            event.reply("Ты кто бля? Пошёл нахуй!").setEphemeral(true).queue();
            return;
        }
        event.reply("Я двухсотый").setEphemeral(true).queue();
        event.getJDA().shutdown();
    }
}
