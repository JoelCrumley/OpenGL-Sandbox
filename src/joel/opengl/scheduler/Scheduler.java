package joel.opengl.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Scheduler {

    public Scheduler() {
        parallelExecutor = Executors.newCachedThreadPool();
        pendingTasks = new ConcurrentLinkedQueue<>();
        scheduledTasks = Collections.synchronizedList(new ArrayList<ScheduledTask>());
    }

    private final ExecutorService parallelExecutor;
    public final ConcurrentLinkedQueue<ScheduledTask> pendingTasks;
    private final List<ScheduledTask> scheduledTasks;

    public void close() throws InterruptedException {
        checkScheduledTasks();
        parallelExecutor.shutdown();

        ScheduledTask task;
        while ((task = pendingTasks.poll()) != null) task.run();

        parallelExecutor.awaitTermination(10L, TimeUnit.SECONDS);
    }

    public boolean runNextTask() {
        ScheduledTask task = pendingTasks.poll();
        if (task == null) return false;

        task.scheduled = false;

        if (!task.cancelled) {
            task.run();
            if (task.shouldRepeat) {
                task.executionsCounter++;
                if (task.repeatExecutions == -1 || task.executionsCounter < task.repeatExecutions) {
                    task.scheduledTime = System.currentTimeMillis() + task.repeatInterval;
                    handleTask(task);
                }
            }
        }

        return !pendingTasks.isEmpty();
    }

    public void handleTask(ScheduledTask task) {
        if (task.cancelled) return;
        long now = System.currentTimeMillis();
        task.scheduled = true;
        if (task.scheduledTime - now < 0) {
            if (task.parallel && !task.shouldRepeat) parallelExecutor.submit(task);
            else pendingTasks.add(task);
        } else {
            synchronized (scheduledTasks) {
                scheduledTasks.add(task);
            }
        }
    }

    public void checkScheduledTasks() { // to be ran every tick
        synchronized (scheduledTasks) {
            long now = System.currentTimeMillis();
            Iterator<ScheduledTask> it = scheduledTasks.iterator();
            while (it.hasNext()) {
                ScheduledTask task = it.next();
                if (task.cancelled) {
                    task.scheduled = false;
                    it.remove();
                } else if (task.scheduledTime - now < 0) {
                    if (task.parallel && !task.shouldRepeat) parallelExecutor.submit(task);
                    else pendingTasks.add(task);
                    it.remove();
                }
            }
        }
    }

}
