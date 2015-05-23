package trilodi.ru.free_lanceru;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.provider.Settings;

import com.loopj.android.http.PersistentCookieStore;

/**
 * Created by REstoreService on 23.05.15.
 */
public class FreeLanceApplication extends Application {
    public FreeLanceApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        super.registerComponentCallbacks(callback);
    }

    @Override
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        super.unregisterComponentCallbacks(callback);
    }

    @Override
    public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        super.registerActivityLifecycleCallbacks(callback);
    }

    @Override
    public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        super.unregisterActivityLifecycleCallbacks(callback);
    }

    @Override
    public void registerOnProvideAssistDataListener(OnProvideAssistDataListener callback) {
        super.registerOnProvideAssistDataListener(callback);
    }

    @Override
    public void unregisterOnProvideAssistDataListener(OnProvideAssistDataListener callback) {
        super.unregisterOnProvideAssistDataListener(callback);
    }

    public static Context getContext() {
        try {
            return (Context) Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication").invoke(null, (Object[]) null);
        } catch (final Exception e1) {
            try {
                return (Context) Class.forName("android.app.AppGlobals")
                        .getMethod("getInitialApplication").invoke(null, (Object[]) null);
            } catch (final Exception e2) {
                throw new RuntimeException("Failed to get application instance");
            }
        }
    }
}
