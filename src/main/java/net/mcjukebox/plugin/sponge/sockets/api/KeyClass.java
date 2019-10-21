package net.mcjukebox.plugin.sponge.sockets.api;

public class KeyClass {
    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    private String keyValue;

    public KeyClass(String keyValue) {
        this.keyValue = keyValue;
    }
}
