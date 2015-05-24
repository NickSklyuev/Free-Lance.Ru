package trilodi.ru.free_lanceru.Components;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Created by REstoreService on 24.05.15.
 */
public class BusProvider extends Bus {
    private static final Bus BUS = new BusProvider();
    private final Handler mainThread = new Handler(Looper.getMainLooper());

    public static Bus getInstance() {
        return BUS;
    }
    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    post(event);
                }
            });
        }
    }
}