package com.example.quanly_rapchieuphim.config.init;

import com.example.quanly_rapchieuphim.entity.ScreeningRoom;
import com.example.quanly_rapchieuphim.entity.Seat;
import com.example.quanly_rapchieuphim.enums.RoomType;
import com.example.quanly_rapchieuphim.enums.SeatType;
import com.example.quanly_rapchieuphim.enums.Status;
import com.example.quanly_rapchieuphim.repository.ScreeningRoomRepository;
import com.example.quanly_rapchieuphim.repository.SeatRepository;
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
@Order(5) // Chạy thứ 5
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomAndSeatInitializer implements ApplicationRunner {

    ScreeningRoomRepository roomRepository;
    SeatRepository seatRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Kiểm tra và khởi tạo dữ liệu ScreeningRoom & Seat...");

        // 1. Tạo 4 phòng chiếu với quy mô khác nhau (Số hàng x Số cột = Sức chứa)
        createRoomWithSeatsIfNotExist("Phòng Chiếu 1 - Standard", RoomType.STANDARD, 8, 10); // 80 ghế
        createRoomWithSeatsIfNotExist("Phòng Chiếu 2 - 3D", RoomType.THREE_D, 10, 12);       // 120 ghế
        createRoomWithSeatsIfNotExist("Phòng Chiếu 3 - IMAX", RoomType.IMAX, 12, 14);        // 168 ghế
        createRoomWithSeatsIfNotExist("Phòng Chiếu 4 - VIP", RoomType.VIP, 5, 8);            // 40 ghế

        log.info("Hoàn tất quy trình kiểm tra Phòng Chiếu và Ghế.");
    }

    private void createRoomWithSeatsIfNotExist(String name, RoomType type, int totalRows, int totalCols) {
        if (!roomRepository.existsByName(name)) {
            // Bước 1: Tạo và lưu Room
            int capacity = totalRows * totalCols;
            ScreeningRoom room = ScreeningRoom.builder()
                    .name(name)
                    .type(type)
                    .capacity(capacity)
                    .status(Status.ENABLE)
                    .build();
            room = roomRepository.save(room);
            log.info("Đã tạo phòng chiếu: {}", name);

            // Bước 2: Tự động sinh danh sách Ghế (Seat) cho phòng vừa tạo
            List<Seat> seatsToSave = new ArrayList<>();
            for (int row = 1; row <= totalRows; row++) {
                for (int col = 1; col <= totalCols; col++) {
                    SeatType seatType;

                    // Phân bổ loại ghế tự động:
                    // - Hàng cuối cùng (row == totalRows): Ghế COUPLE
                    // - Khoảng giữa: Ghế VIP
                    // - Những hàng đầu: Ghế STANDARD
                    if (row == totalRows) {
                        seatType = SeatType.COUPLE;
                    } else if (row >= (totalRows / 2) && row < totalRows) {
                        seatType = SeatType.VIP;
                    } else {
                        seatType = SeatType.STANDARD;
                    }

                    // Riêng đối với phòng VIP, tất cả ghế đều là VIP (trừ hàng cuối couple)
                    if (type == RoomType.VIP && seatType == SeatType.STANDARD) {
                        seatType = SeatType.VIP;
                    }

                    Seat seat = Seat.builder()
                            .screeningRoom(room)
                            .rowPosition(row)
                            .columnPosition(col)
                            .type(seatType)
                            .build();
                    seatsToSave.add(seat);
                }
            }

            // Lưu toàn bộ ghế vào database
            seatRepository.saveAll(seatsToSave);
            log.info("Đã tạo {} ghế cho {}.", seatsToSave.size(), name);
        }
    }
}