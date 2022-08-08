package joel.opengl.scheduler;

public abstract class ScheduledTask implements Runnable {

    public long scheduledTime;
    public boolean parallel = false, scheduled = false;
    public volatile boolean cancelled = false;

    private void runTask(Scheduler scheduler, long sysTime, boolean parallel) {
        if (scheduled) return;
        this.scheduledTime = sysTime;
        this.parallel = parallel;
        cancelled = false;
        scheduler.handleTask(this);
    }

    public void runTask(Scheduler scheduler) {
        runTask(scheduler, 0L, false);
    }

    public void runTaskLater(Scheduler scheduler, long wait) {
        runTask(scheduler, System.currentTimeMillis() + wait, false);
    }

    public void runTaskLater(Scheduler scheduler, float seconds) {
        runTaskLater(scheduler, (long) (seconds * 1000.0f));
    }

    public void runParallelTask(Scheduler scheduler) {
        runTask(scheduler, 0L, true);
    }

    public void runParallelTaskLater(Scheduler scheduler, long wait) {
        runTask(scheduler, System.currentTimeMillis() + wait, true);
    }

    public void runParallelTaskLater(Scheduler scheduler, float seconds) {
        runParallelTaskLater(scheduler, (long) (seconds * 1000.0f));
    }

}
