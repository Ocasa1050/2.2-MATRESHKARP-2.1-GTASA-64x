package ru.edgar.space.diagnostics;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Writes local game diagnostics and makes the file easy to share from the launcher. */
public final class CrashLogManager {
    private static final String TAG = "CrashLogManager";
    private static final String PREFS = "diagnostic_session";
    private static final String KEY_GAME_ACTIVE = "game_session_active";
    private static final String KEY_LAST_STAGE = "last_game_stage";
    private static final Object FILE_LOCK = new Object();
    private static boolean installed;

    private CrashLogManager() { }

    public static void initialize(Context context) {
        Context appContext = context.getApplicationContext();
        synchronized (CrashLogManager.class) {
            if (installed) return;
            installed = true;
        }

        SharedPreferences prefs = appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (prefs.getBoolean(KEY_GAME_ACTIVE, false)) {
            append(appContext, "==== Previous game session ended without a normal close ====");
            append(appContext, "Last recorded game stage: " + prefs.getString(KEY_LAST_STAGE, "unknown"));
            append(appContext, "This can indicate a native crash, a forced stop, or the OS terminating the process.");
            prefs.edit().putBoolean(KEY_GAME_ACTIVE, false).commit();
        }

        append(appContext, "==== Launcher started " + timestamp() + " ====");
        append(appContext, "App version: " + getVersion(appContext)
                + "; Android: " + Build.VERSION.RELEASE
                + " (API " + Build.VERSION.SDK_INT + ")"
                + "; ABI: " + Build.SUPPORTED_ABIS[0]);

        final Thread.UncaughtExceptionHandler previous = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
            append(appContext, "==== FATAL JAVA EXCEPTION ====");
            append(appContext, "Thread: " + thread.getName());
            StringWriter stack = new StringWriter();
            error.printStackTrace(new PrintWriter(stack));
            append(appContext, stack.toString());
            if (previous != null) {
                previous.uncaughtException(thread, error);
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(10);
            }
        });
    }

    public static void beginGameSession(Context context) {
        SharedPreferences prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_GAME_ACTIVE, true)
                .putString(KEY_LAST_STAGE, "launcher.start_server_click")
                .commit();
        recordEvent(context, "launcher.start_server_click");
    }

    public static void recordEvent(Context context, String event) {
        if (context == null || event == null) return;
        append(context.getApplicationContext(), "[JAVA] " + event);
        SharedPreferences prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (prefs.getBoolean(KEY_GAME_ACTIVE, false)) {
            prefs.edit().putString(KEY_LAST_STAGE, event).commit();
        }
    }

    public static void endGameSession(Context context) {
        recordEvent(context, "game_activity_destroyed_normally");
        context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_GAME_ACTIVE, false).commit();
    }

    public static void shareLog(Context context) {
        File logFile = getLogFile(context);
        if (!logFile.exists()) {
            append(context.getApplicationContext(), "No native log was produced yet.");
        }
        try {
            Uri uri = FileProvider.getUriForFile(context,
                    context.getPackageName() + ".provider", logFile);
            Intent send = new Intent(Intent.ACTION_SEND);
            send.setType("text/plain");
            send.putExtra(Intent.EXTRA_SUBJECT, "SA-MP diagnostic log");
            send.putExtra(Intent.EXTRA_TEXT, "Diagnostic log from the last game session.");
            send.putExtra(Intent.EXTRA_STREAM, uri);
            send.setClipData(ClipData.newUri(context.getContentResolver(), "samp_log.txt", uri));
            send.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(send, "Share diagnostic log"));
        } catch (Exception error) {
            Log.e(TAG, "Unable to share diagnostic log", error);
        }
    }

    private static File getLogFile(Context context) {
        File root = context.getExternalFilesDir(null);
        if (root != null) return new File(root, "samp_log.txt");
        File logs = new File(context.getFilesDir(), "logs");
        if (!logs.exists() && !logs.mkdirs()) {
            Log.e(TAG, "Unable to create internal log directory: " + logs);
        }
        return new File(logs, "samp_log.txt");
    }

    private static void append(Context context, String text) {
        synchronized (FILE_LOCK) {
            File file = getLogFile(context);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) return;
            try (OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(file, true), StandardCharsets.UTF_8)) {
                writer.write("[" + timestamp() + "] " + text + "\n");
                writer.flush();
            } catch (Exception error) {
                Log.e(TAG, "Unable to append diagnostic log", error);
            }
        }
    }

    private static String timestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(new Date());
    }

    private static String getVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception ignored) {
            return "unknown";
        }
    }
}
