package xyz.black.box.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CrashLogger implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashLogger";
    private final Context context;
    private final Thread.UncaughtExceptionHandler defaultHandler;
    private static CrashLogger instance;
    private static final String LOG_DIR_NAME = "BlackBoxLogs";
    private final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS", Locale.getDefault());
    private File logFile;
    private ScheduledExecutorService logCollector;
    private Process logcatProcess;
    private FileOutputStream logStream;

    private CrashLogger(Context context) {
        this.context = context;
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        initializeLogFile();
        startLogCollection();
    }

    private void initializeLogFile() {
        File logDir = getLogDirectory();
        String timestamp = timestampFormat.format(new Date());
        logFile = new File(logDir, "blackbox_logs_" + timestamp + ".log");
    }

    private void startLogCollection() {
        try {
            // Start collecting logs
            logStream = new FileOutputStream(logFile, true);

            // Write initial timestamp
            String initialTimestamp = timestampFormat.format(new Date());
            logStream.write(("\n=== BlackBox Logging Started at " + initialTimestamp + " ===\n\n").getBytes());

            // Start logcat process to capture system logs
            ProcessBuilder processBuilder = new ProcessBuilder("logcat", "-v", "time");
            processBuilder.redirectErrorStream(true);
            logcatProcess = processBuilder.start();

            // Start a thread to read logcat output
            new Thread(() -> {
                try {
                    java.io.InputStream inputStream = logcatProcess.getInputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        String logLine = new String(buffer, 0, bytesRead);
                        logStream.write(logLine.getBytes());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error reading logcat", e);
                }
            }).start();

            // Start periodic log collection
            logCollector = Executors.newSingleThreadScheduledExecutor();
            logCollector.scheduleAtFixedRate(this::collectLogs, 0, 5, TimeUnit.SECONDS);

        } catch (Exception e) {
            Log.e(TAG, "Error starting log collection", e);
        }
    }

    private void collectLogs() {
        try {
            // Collect system logs
            String systemLog = getSystemLog();
            if (systemLog != null && !systemLog.isEmpty()) {
                logStream.write((systemLog + "\n").getBytes());
            }

            // Collect virtual app logs
            String virtualAppLog = getVirtualAppLog();
            if (virtualAppLog != null && !virtualAppLog.isEmpty()) {
                logStream.write((virtualAppLog + "\n").getBytes());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error collecting logs", e);
        }
    }

    private String getSystemLog() {
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            java.io.InputStream inputStream = process.getInputStream();
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            return new String(buffer);
        } catch (Exception e) {
            Log.e(TAG, "Error getting system log", e);
            return null;
        }
    }

    private String getVirtualAppLog() {
        try {
            // Get list of running processes
            Process process = Runtime.getRuntime().exec("ps");
            java.io.InputStream inputStream = process.getInputStream();
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            String processes = new String(buffer);

            // Filter for virtual apps and get their logs
            StringBuilder virtualAppLogs = new StringBuilder();
            for (String line : processes.split("\n")) {
                if (line.contains("com.android.vending") || line.contains("com.google.android.gms")) {
                    virtualAppLogs.append(line).append("\n");
                }
            }
            return virtualAppLogs.toString();
        } catch (Exception e) {
            Log.e(TAG, "Error getting virtual app log", e);
            return null;
        }
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new CrashLogger(context);
            Thread.setDefaultUncaughtExceptionHandler(instance);
            Log.d(TAG, "CrashLogger initialized");
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        try {
            Log.d(TAG, "Uncaught exception detected");
            saveCrashLog(throwable);
        } catch (Exception e) {
            Log.e(TAG, "Error saving crash log", e);
        } finally {
            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, throwable);
            }
        }
    }

    private File getLogDirectory() {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File logDir = new File(documentsDir, LOG_DIR_NAME);
        
        if (!logDir.exists()) {
            boolean created = logDir.mkdirs();
            Log.d(TAG, "Log directory created: " + created + " at " + logDir.getAbsolutePath());
        }
        return logDir;
    }

    private void saveCrashLog(Throwable throwable) {
        try {
            String timestamp = timestampFormat.format(new Date());
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            String stackTrace = sw.toString();

            String logContent = "\nCrash at " + timestamp + "\n" +
                             "Package: " + context.getPackageName() + "\n" +
                             "Version: " + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName + "\n" +
                             stackTrace + "\n";

            logStream.write(logContent.getBytes());
            logStream.flush();
            
            Log.d(TAG, "Crash log saved successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error saving crash log", e);
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        String timestamp = timestampFormat.format(new Date());
        try {
            if (logStream != null) {
                logStream.write(("\n=== Logging Ended at " + timestamp + " ===\n").getBytes());
                logStream.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in finalize", e);
        }
        if (logcatProcess != null) {
            logcatProcess.destroy();
        }
        if (logCollector != null) {
            logCollector.shutdown();
        }
    }
} 