package net.mcjukebox.plugin.sponge.api.models;

import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.api.ResourceType;
import org.json.JSONObject;

public class Media {

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public boolean isLooping() {
        return looping;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    public int getFadeDuration() {
        return fadeDuration;
    }

    public void setFadeDuration(int fadeDuration) {
        this.fadeDuration = fadeDuration;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    private ResourceType type = ResourceType.SOUND_EFFECT;
    private String URL = "";

    //The following options are only supported for the MUSIC resource type
    private int volume = 100;
    private boolean looping = true;
    private int fadeDuration = -1;
    private long startTime = -1;
    private String channel = "default";

    public Media(ResourceType type, String URL) {
        setType(type);
        setURL(URL);
    }

    public Media(ResourceType type, String URL, JSONObject options){
        setType(type);
        setURL(URL);

        loadOptions(options);
    }

    public void loadOptions(JSONObject options) {
        if(options.has("volume")) {
            if(options.get("volume") instanceof Integer) {
                if (options.getInt("volume") <= 100 && options.getInt("volume") >= 0) {
                    volume = options.getInt("volume");
                }
            }
        }

        if(options.has("looping")) {
            if(options.get("looping") instanceof Boolean) {
                looping = options.getBoolean("looping");
            }
        }

        if(options.has("fadeDuration")) {
            if(options.get("fadeDuration") instanceof Integer) {
                if (options.getInt("fadeDuration") <= 30 && options.getInt("fadeDuration") >= 0) {
                    fadeDuration = options.getInt("fadeDuration");
                }
            }
        }

        if(options.has("startTime")) {
            if(options.get("startTime") instanceof String && options.getString("startTime").equalsIgnoreCase("now")) {
                startTime = MCJukebox.getInstance().getTimeUtils().currentTimeMillis();
            } else if(options.get("startTime") instanceof Long || options.get("startTime") instanceof Integer) {
                startTime = options.getLong("startTime");
            }
        }

        if(options.has("channel")) {
            if(options.get("channel") instanceof String) {
                this.channel = options.getString("channel");
            }
        }
    }

}
