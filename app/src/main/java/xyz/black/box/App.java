package xyz.black.box;

import android.app.Application;
import android.content.Context;


import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.configuration.ClientConfiguration;
import xyz.black.box.utils.CrashLogger;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BlackBoxCore.get().doCreate();
        CrashLogger.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
            BlackBoxCore.get().doAttachBaseContext(this, new ClientConfiguration() {
                @Override
                public String getHostPackageName() {
                    return base.getPackageName();
                }
            });
    }
}
