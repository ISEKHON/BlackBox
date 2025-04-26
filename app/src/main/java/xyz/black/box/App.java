package xyz.black.box;

import android.app.Application;
import android.content.Context;

import top.niunaijun.bcore.BlackBoxCore;
import top.niunaijun.bcore.app.configuration.ClientConfiguration;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BlackBoxCore.get().doCreate();
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
