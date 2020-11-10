package vn.hunghd.flutterdownloader;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by suli on 2020/11/10
 *
 * 使用线程池执行任务，避免androidx WorkManager在某些手机上不执行，如OPPO
 **/
class WorkManager {
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Map<UUID, Future<?>> taskMap = new ConcurrentHashMap<>();
    private final Map<UUID, Worker> workMap = new ConcurrentHashMap<>();

    private static class Instance {
        private final static WorkManager instance = new WorkManager();
    }

    public static WorkManager getInstance() {
        return Instance.instance;
    }

    public void enqueue(final Worker worker) {
        Future<?> future = executor.submit(new Runnable() {
            @Override
            public void run() {
                workMap.put(worker.getId(), worker);
                worker.doWork();
            }
        });
        taskMap.put(worker.getId(), future);
    }

    public void cancelWorkById(UUID uuid) {
        Worker worker = workMap.get(uuid);
        if (worker != null) {
            worker.stop();
            Future<?> future = taskMap.get(uuid);
            if (future != null && !future.isCancelled()) {
                future.cancel(false);
                taskMap.remove(uuid);
            }
            workMap.remove(uuid);
        }
    }

    public void cancelAllWorkByTag() {
        for (Worker worker : workMap.values()) {
            worker.stop();
            Future<?> future = taskMap.get(worker.getId());
            if (future != null && !future.isCancelled()) {
                future.cancel(false);
            }
        }
        workMap.clear();
        taskMap.clear();
    }
}
