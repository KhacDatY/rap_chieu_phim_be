package com.example.quanly_rapchieuphim.config.init;

import com.example.quanly_rapchieuphim.entity.TicketPrice;
import com.example.quanly_rapchieuphim.enums.DayType;
import com.example.quanly_rapchieuphim.enums.RoomType;
import com.example.quanly_rapchieuphim.enums.SeatType;
import com.example.quanly_rapchieuphim.enums.TimeSlot;
import com.example.quanly_rapchieuphim.repository.TicketPriceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Order(6) // Chạy sau RoomAndSeat
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TicketPriceInitializer implements ApplicationRunner {

    TicketPriceRepository ticketPriceRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Kiểm tra và khởi tạo cấu hình Bảng giá vé (TicketPrice)...");
        List<TicketPrice> pricesToSave = new ArrayList<>();

        // Giá gốc (Phòng Standard, Ghế Standard, Ngày thường, Giờ bình thường)
        double basePrice = 60000.0;

        for (RoomType room : RoomType.values()) {
            for (SeatType seat : SeatType.values()) {
                for (DayType day : DayType.values()) {
                    for (TimeSlot time : TimeSlot.values()) {

                        if (!ticketPriceRepository.existsByRoomTypeAndSeatTypeAndDayTypeAndTimeSlot(room, seat, day, time)) {
                            double finalPrice = basePrice;

                            // Phụ thu theo Phòng
                            if (room == RoomType.THREE_D) finalPrice += 30000;
                            else if (room == RoomType.IMAX) finalPrice += 50000;
                            else if (room == RoomType.VIP) finalPrice += 80000;

                            // Phụ thu theo Ghế
                            if (seat == SeatType.VIP) finalPrice += 15000;
                            else if (seat == SeatType.COUPLE) finalPrice += 45000;

                            // Phụ thu Ngày lễ / Cuối tuần
                            if (day == DayType.WEEKEND) finalPrice += 20000;
                            else if (day == DayType.HOLIDAY) finalPrice += 30000;

                            // Phụ thu Giờ vàng
                            if (time == TimeSlot.GOLDEN_HOUR) finalPrice += 10000;

                            pricesToSave.add(TicketPrice.builder()
                                    .roomType(room)
                                    .seatType(seat)
                                    .dayType(day)
                                    .timeSlot(time)
                                    .price(finalPrice)
                                    .build());
                        }
                    }
                }
            }
        }

        if (!pricesToSave.isEmpty()) {
            ticketPriceRepository.saveAll(pricesToSave);
            log.info("Đã tạo mới {} cấu hình giá vé.", pricesToSave.size());
        } else {
            log.info("Bảng giá vé đã được cấu hình đầy đủ.");
        }
    }
}