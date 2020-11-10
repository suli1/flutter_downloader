package vn.hunghd.flutterdownloader;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.UUID;

/**
 * Created by suli on 2020/11/10
 **/
abstract class Worker {
    private final Context mAppContext;
    private final UUID mId;
    private volatile boolean mIsStopped = false;

    public Worker(@NonNull Context context) {
        this.mAppContext = context.getApplicationContext();
        this.mId = UUID.randomUUID();
    }

    abstract void doWork();

    public final @NonNull
    Context getApplicationContext() {
        return mAppContext;
    }

    public UUID getId() {
        return mId;
    }

    public boolean isStopped() {
        return mIsStopped;
    }

    public void stop() {
        mIsStopped = true;
    }
}
