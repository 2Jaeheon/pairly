package pairly.server;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class PomodoroTimer {
    private static final int POMODORO_SECONDS = 300;

    private final int pomodoroSeconds;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> future;
    private final AtomicInteger timeLeft;

    public PomodoroTimer() {
        this(POMODORO_SECONDS);
    }

    public PomodoroTimer(int pomodoroSeconds) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.pomodoroSeconds = pomodoroSeconds;
        this.timeLeft = new AtomicInteger(pomodoroSeconds);
    }

    public synchronized void start(Consumer<String> tick, Runnable expired) {
        stop();
        this.timeLeft.set(this.pomodoroSeconds);

        future = scheduler.scheduleAtFixedRate(() -> {
            int next = timeLeft.decrementAndGet();

            try {
                tick.accept(formatTime(next));

                if (next <= 0) {
                    try {
                        expired.run();
                    } finally {
                        timeLeft.set(this.pomodoroSeconds);
                    }
                }
            } catch (Exception e) {
                // 로그만 남기고 타이머는 계속 돌아가야
                e.printStackTrace();
            }

        }, 0, 1, TimeUnit.SECONDS);
    }

    public synchronized void stop() {
        if (future != null) {
            future.cancel(true);
        }
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
