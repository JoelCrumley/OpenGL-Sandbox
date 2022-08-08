package joel.opengl.scheduler;

public abstract class ScheduledTask implements Runnable {

    public long scheduledTime;
    public boolean parallel = false, scheduled = false;
    public volatile boolean cancelled = false;

    public boolean shouldRepeat = false;
    public long repeatInterval;
    public int repeatExecutions, executionsCounter = 0;

    private void runTask(Scheduler scheduler, long sysTime, boolean parallel) {
        if (scheduled) return;
        this.scheduledTime = sysTime;
        this.parallel = parallel;
        cancelled = false;
        scheduler.handleTask(this);
    }

    public void cancel() {
        cancelled = true;
    }

    public void runRepeatingTask(Scheduler scheduler, long wait, long interval, int executions) {
        if (scheduled) return;
        this.shouldRepeat = true;
        this.scheduledTime = System.currentTimeMillis() + wait;
        this.repeatInterval = interval;
        this.repeatExecutions = executions;
        cancelled = false;
        scheduler.handleTask(this);
    }

    public void runRepeatingTask(Scheduler scheduler, long wait, long interval) {
        runRepeatingTask(scheduler, wait, interval, -1);
    }

    public void runRepeatingTask(Scheduler scheduler, float wait, float interval) {
        runRepeatingTask(scheduler, (long) (wait * 1000.0f), (long) (interval * 1000.0f), -1);
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
