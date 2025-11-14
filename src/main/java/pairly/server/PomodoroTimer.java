package pairly.server;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class PomodoroTimer {
    private static final int POMODORO_SECONDS = 300;
    private static final long ONE_SECOND_IN_MS = 1000;

    private final Timer timer;
    private int timeLeftInSeconds;
    private TimerTask currentTimerTask;

    public PomodoroTimer() {
        this.timer = new Timer(true);
        this.timeLeftInSeconds = POMODORO_SECONDS;
    }

    public void start(Consumer<String> tick, Runnable expired) {
        stop();

        this.timeLeftInSeconds = POMODORO_SECONDS;

        this.currentTimerTask = new TimerTask() {
            @Override
            public void run() {
                timeLeftInSeconds--;

                tick.accept(formatTime(timeLeftInSeconds));

                if (timeLeftInSeconds <= 0) {
                    expired.run(); // 콜백
                    timeLeftInSeconds = POMODORO_SECONDS; // 타이머 리셋
                }
            }
        };

        timer.scheduleAtFixedRate(currentTimerTask, 0, ONE_SECOND_IN_MS);
    }

    public void stop() {
        if (currentTimerTask != null) {
            currentTimerTask.cancel();
        }
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
