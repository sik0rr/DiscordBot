package com.sik0r.discordbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class GuildMusicManager {

    private TrackScheduler trackScheduler;
    private MusicOrderHandler musicOrderHandler;


    public GuildMusicManager(AudioPlayerManager manager) {
        AudioPlayer player = manager.createPlayer();
        trackScheduler = new TrackScheduler(player);
        player.addListener(trackScheduler);
        musicOrderHandler = new MusicOrderHandler(player);
    }

    public TrackScheduler getTrackScheduler() {
        return trackScheduler;
    }

    public MusicOrderHandler getMusicOrderHandlerHandler() {
        return musicOrderHandler;
    }
}
