package com.sik0r.discordbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;

import java.util.*;
import java.util.concurrent.BlockingQueue;

public class PlayerManager {
    private static PlayerManager INSTANCE;
    private Map<Long, GuildMusicManager> guildMusicManagers = new HashMap<>();
    private AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();

    private PlayerManager() {
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
    }

    public static PlayerManager get() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }

    public GuildMusicManager getGuildMusicManager(Guild guild) {
        return guildMusicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            GuildMusicManager musicManager = new GuildMusicManager(audioPlayerManager);
            guild.getAudioManager().setSendingHandler(musicManager.getMusicOrderHandlerHandler());
            return musicManager;
        });
    }

    public void play(Guild guild, String trackURL) {
        GuildMusicManager guildMusicManager = getGuildMusicManager(guild);
        audioPlayerManager.loadItemOrdered(guildMusicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                guildMusicManager.getTrackScheduler().addToQueue(audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                audioPlaylist.getTracks().forEach(track -> guildMusicManager.getTrackScheduler().addToQueue(track));
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });

    }

    public void playByName(Guild guild, String track) {
        GuildMusicManager guildMusicManager = getGuildMusicManager(guild);
        audioPlayerManager.loadItemOrdered(guildMusicManager, track, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                guildMusicManager.getTrackScheduler().addToQueue(audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                guildMusicManager.getTrackScheduler().addToQueue(audioPlaylist.getTracks().get(0));
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });
    }

    public void playWithShuffle(Guild guild, String track) {
        GuildMusicManager guildMusicManager = getGuildMusicManager(guild);
        audioPlayerManager.loadItemOrdered(guildMusicManager, track, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                guildMusicManager.getTrackScheduler().addToQueue(audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                List<AudioTrack> shuffledPlaylist = shuffle(audioPlaylist);
                shuffledPlaylist.forEach(track -> guildMusicManager.getTrackScheduler().addToQueue(track));
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });
    }

    public void skip(Guild guild) {
        GuildMusicManager guildMusicManager = getGuildMusicManager(guild);
        guildMusicManager.getTrackScheduler().skip();
    }

    public String currentQueue(Guild guild) {
        GuildMusicManager guildMusicManager = getGuildMusicManager(guild);
        BlockingQueue<AudioTrack> queue = guildMusicManager.getTrackScheduler().getQueue();
        StringBuilder sb = new StringBuilder();
        AudioTrack currentTrack = guildMusicManager.getTrackScheduler().getAudioPlayer().getPlayingTrack();
        if (currentTrack != null) {
            sb.append("Сейчас играет: ").append(currentTrack.getInfo().title);
        } else {
            sb.append("Сейчас ничего не играет!");
        }
        if (queue.isEmpty()) {
            if (currentTrack != null) {
                sb.append("\nНет треков, кроме текущего!");
                return sb.toString();
            }
            sb.append("\nПлейлист пуст!");
            return sb.toString();
        }
        sb.append("\n").append("Следующие в очереди:");
        int i = 1;
        for (AudioTrack track : queue) {
            if (i > 5) {
                sb.append("\n").append("И ещё ").append(queue.size()-5).append(" треков.");
                return sb.toString();
            }
            sb.append("\n").append(i).append(". ").append(track.getInfo().title);
            i++;
        }
        return sb.toString();
    }

    public void clearQueue(Guild guild) {
        GuildMusicManager guildMusicManager = getGuildMusicManager(guild);
        guildMusicManager.getTrackScheduler().getQueue().clear();
        guildMusicManager.getTrackScheduler().getAudioPlayer().stopTrack();
    }

    public void shuffle(Guild guild) {
        GuildMusicManager guildMusicManager = getGuildMusicManager(guild);
        BlockingQueue<AudioTrack> queue = guildMusicManager.getTrackScheduler().getQueue();
        Object[] tracks = queue.toArray();
        List<AudioTrack> shuffledTracks = new ArrayList<>();
        for (Object track : tracks) {
            if (track instanceof AudioTrack) {
                shuffledTracks.add((AudioTrack) track);
            }
        }
        Collections.shuffle(shuffledTracks);
        queue.clear();
        queue.addAll(shuffledTracks);
    }

    public List<AudioTrack> shuffle(AudioPlaylist playlist) {
        List<AudioTrack> tracks = playlist.getTracks();
        Collections.shuffle(tracks);
        return tracks;
    }
}
