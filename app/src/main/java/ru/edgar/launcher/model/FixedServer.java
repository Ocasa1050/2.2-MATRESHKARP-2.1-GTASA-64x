package ru.edgar.launcher.model;

public final class FixedServer {
    public static final String HOST = "188.127.241.74";
    public static final int PORT = 1255;
    public static final int DEFAULT_ID = 1;

    private FixedServer() {
    }

    public static Servers create(Integer id) {
        int serverId = id == null ? DEFAULT_ID : id;
        return new Servers(
                "SPACE RP",
                "33AAD9",
                0,
                true,
                false,
                HOST,
                PORT,
                serverId
        );
    }
}
