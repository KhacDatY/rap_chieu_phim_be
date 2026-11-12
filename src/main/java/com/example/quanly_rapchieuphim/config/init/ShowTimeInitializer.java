package com.example.quanly_rapchieuphim.config.init;

import com.example.quanly_rapchieuphim.entity.Movie;
import com.example.quanly_rapchieuphim.entity.ScreeningRoom;
import com.example.quanly_rapchieuphim.entity.ShowTime;
import com.example.quanly_rapchieuphim.enums.MovieStatus;
import com.example.quanly_rapchieuphim.repository.MovieRepository;
import com.example.quanly_rapchieuphim.repository.ScreeningRoomRepository;
import com.example.quanly_rapchieuphim.repository.ShowTimeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Order(7) // Chạy cuối cùng sau khi đã có Phim và Phòng chiếu
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShowTimeInitializer implements ApplicationRunner {

    ShowTimeRepository showTimeRepository;
    MovieRepository movieRepository;
    ScreeningRoomRepository roomRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Kiểm tra và khởi tạo dữ liệu Suất chiếu (ShowTime)...");

        List<Movie> nowShowingMovies = movieRepository.findByStatus(MovieStatus.NOW_SHOWING);
        List<ScreeningRoom> rooms = roomRepository.findAll();

        if (nowShowingMovies.isEmpty() || rooms.isEmpty()) {
            log.warn("Không có Phim đang chiếu hoặc Phòng chiếu. Bỏ qua tạo suất chiếu.");
            return;
        }

        List<ShowTime> showTimesToSave = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // Khung giờ chiếu cố định trong ngày
        LocalTime[] timeSlots = {
                LocalTime.of(9, 0),   // 09:00 Sáng
                LocalTime.of(13, 30), // 13:30 Chiều
                LocalTime.of(18, 0),  // 18:00 Tối
                LocalTime.of(21, 30)  // 21:30 Đêm
        };

        // Tạo lịch chiếu cho 2 ngày: Hôm nay và Ngày mai
        for (int dayOffset = 0; dayOffset <= 1; dayOffset++) {
            LocalDate scheduleDate = today.plusDays(dayOffset);

            for (int roomIndex = 0; roomIndex < rooms.size(); roomIndex++) {
                ScreeningRoom room = rooms.get(roomIndex);

                for (int timeIndex = 0; timeIndex < timeSlots.length; timeIndex++) {
                    // Chọn phim xoay vòng để phòng nào, giờ nào cũng có phim khác nhau
                    Movie movie = nowShowingMovies.get((roomIndex + timeIndex + dayOffset) % nowShowingMovies.size());

                    LocalDateTime startDateTime = LocalDateTime.of(scheduleDate, timeSlots[timeIndex]);
                    // Giờ kết thúc = Giờ bắt đầu + thời lượng phim (duration)
                    LocalDateTime endDateTime = startDateTime.plusMinutes(movie.getDuration());

                    if (!showTimeRepository.existsByScreeningRoomIdAndStartDate(room.getId(), startDateTime)) {
                        showTimesToSave.add(ShowTime.builder()
                                .movie(movie)
                                .screeningRoom(room)
                                .startDate(startDateTime)
                                .endDate(endDateTime)
                                .build());
                    }
                }
            }
        }

        if (!showTimesToSave.isEmpty()) {
            showTimeRepository.saveAll(showTimesToSave);
            log.info("Đã tạo mới {} suất chiếu cho 2 ngày tới.", showTimesToSave.size());
        } else {
            log.info("Lịch chiếu phim đã được xếp đầy đủ.");
        }
    }
}