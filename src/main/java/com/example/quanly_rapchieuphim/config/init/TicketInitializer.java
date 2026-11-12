package com.example.quanly_rapchieuphim.config.init;

import com.example.quanly_rapchieuphim.entity.*;
import com.example.quanly_rapchieuphim.enums.*;
import com.example.quanly_rapchieuphim.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Order(8) // Chạy cuối cùng
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TicketInitializer implements ApplicationRunner {

    TicketRepository ticketRepository;
    ShowTimeRepository showTimeRepository;
    SeatRepository seatRepository;
    TicketPriceRepository ticketPriceRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Kiểm tra và khởi tạo dữ liệu Ticket...");

        // Nếu đã có vé rồi thì bỏ qua để tránh tạo lặp dữ liệu khổng lồ
        if (ticketRepository.count() > 0) {
            log.info("Dữ liệu Ticket đã tồn tại. Bỏ qua khởi tạo.");
            return;
        }

        List<ShowTime> showTimes = showTimeRepository.findAll();
        if (showTimes.isEmpty()) {
            log.warn("Không có suất chiếu nào để tạo vé.");
            return;
        }

        List<Ticket> ticketsToSave = new ArrayList<>();

        for (ShowTime showTime : showTimes) {
            ScreeningRoom room = showTime.getScreeningRoom();
            List<Seat> seats = seatRepository.findByScreeningRoomId(room.getId());

            // 1. Phân loại Ngày thường hay Cuối tuần
            DayOfWeek dayOfWeek = showTime.getStartDate().getDayOfWeek();
            DayType dayType = (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY)
                    ? DayType.WEEKEND
                    : DayType.WEEKDAY;

            // 2. Phân loại Khung giờ (Giả sử 17:00 đến 22:00 là Giờ Vàng)
            LocalTime startTime = showTime.getStartDate().toLocalTime();
            TimeSlot timeSlot = (startTime.isAfter(LocalTime.of(16, 59)) && startTime.isBefore(LocalTime.of(22, 1)))
                    ? TimeSlot.GOLDEN_HOUR
                    : TimeSlot.NORMAL;

            // 3. Quét từng ghế để lấy cấu hình giá chuẩn và tạo vé
            for (Seat seat : seats) {
                TicketPrice ticketPrice = ticketPriceRepository.findByRoomTypeAndSeatTypeAndDayTypeAndTimeSlot(
                        room.getType(), seat.getType(), dayType, timeSlot
                ).orElseThrow(() -> new RuntimeException("Không tìm thấy cấu hình giá vé phù hợp!"));

                Ticket ticket = Ticket.builder()
                        .showTime(showTime)
                        .seat(seat)
                        .ticketPrice(ticketPrice)
                        .status(TicketStatus.AVAILABLE) // Vé trống sẵn sàng mở bán
                        .build();

                ticketsToSave.add(ticket);
            }
        }

        // Batch insert để tăng tốc độ lưu xuống DB
        if (!ticketsToSave.isEmpty()) {
            ticketRepository.saveAll(ticketsToSave);
            log.info("Đã tạo thành công {} vé cho {} suất chiếu.", ticketsToSave.size(), showTimes.size());
        }
    }
}