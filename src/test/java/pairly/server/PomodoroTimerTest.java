package pairly.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PomodoroTimerTest {
    @Mock
    private Consumer<String> mockTick;

    @Mock
    private Runnable mockExpired;

    private PomodoroTimer timer;

    @AfterEach
    void tearDown() {
        if (timer != null) {
            timer.stop();
        }
    }

    @Nested
    @DisplayName("스케줄링과 콜백이 정상적으로 동작하는지 테스트")
    class SchedulingAndCallBackTest {
        @Test
        @DisplayName("1초 타이머가 만료되면 콜백이 1번 호출된다")
        void shouldCallExpiredWhenTimerExpires() throws InterruptedException {
            // given
            timer = new PomodoroTimer(1);

            // when
            timer.start(mockTick, mockExpired);
            Thread.sleep(100);

            // then
            verify(mockExpired, times(1)).run(); // 만료 검증
            verify(mockTick, times(1)).accept("00:00"); // 타이머 검증
        }

        @Test
        @DisplayName("3초 타이머는 onTick 콜백을 순서대로 호출한다")
        void shouldCallOnTickInOrder() throws InterruptedException {
            // given
            timer = new PomodoroTimer(3);
            ArgumentCaptor<String> timeCaptor = ArgumentCaptor.forClass(String.class);

            // when
            timer.start(mockTick, mockExpired);
            Thread.sleep(2500);

            // then
            verify(mockTick, times(3)).accept(timeCaptor.capture());
            assertThat(timeCaptor.getAllValues()).containsExactly("00:02", "00:01", "00:00");
            verify(mockExpired, times(1)).run();
        }

        @Test
        @DisplayName("stop() 호출 시 타이머가 즉시 중지된다")
        void shouldStopCallBack() throws InterruptedException {
            // given
            timer = new PomodoroTimer(5);

            // when
            timer.start(mockTick, mockExpired);
            timer.stop();
            Thread.sleep(500);

            // then
            verify(mockTick, never()).accept(anyString());
            verify(mockExpired, never()).run();
        }

        @Test
        @DisplayName("콜백(tick)에서 예외가 발생해도 타이머는 멈추지 않는다")
        void shouldNotKillTimerWhenCallbackFail() throws InterruptedException {
            // given
            timer = new PomodoroTimer(3);

            // when
            doThrow(new RuntimeException("콜백 실패!!!"))
                    .when(mockTick).accept("00:01");
            timer.start(mockTick, mockExpired);
            Thread.sleep(2500);

            // then
            verify(mockTick, times(1)).accept("00:02");
            verify(mockTick, times(1)).accept("00:01");
            verify(mockTick, times(1)).accept("00:00");
            verify(mockExpired, times(1)).run();
        }
    }
}