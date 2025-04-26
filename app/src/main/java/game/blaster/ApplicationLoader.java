package game.blaster;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import top.niunaijun.bcore.BlackBoxCore;
import top.niunaijun.bcore.app.configuration.ClientConfiguration;


public class ApplicationLoader extends Application {

    public static ApplicationLoader applicationLoader;
    private SharedPreferences mPreferences;

    public static ApplicationLoader getInstance() {
        return applicationLoader;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mPreferences = base.getSharedPreferences("config", Context.MODE_MULTI_PROCESS);
        BlackBoxCore.get().doAttachBaseContext(this, new ClientConfiguration() {
            @Override
            public String getHostPackageName() {
                return base.getPackageName();
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationLoader = this;
        BlackBoxCore.get().doCreate();
    }

    public SharedPreferences getPreferences() {
        return getInstance().mPreferences;
    }

}
