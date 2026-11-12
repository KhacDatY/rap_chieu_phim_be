package com.example.quanly_rapchieuphim.config.init;

import com.example.quanly_rapchieuphim.entity.*;
import com.example.quanly_rapchieuphim.enums.MemberRank;
import com.example.quanly_rapchieuphim.repository.UserRepository;
import com.example.quanly_rapchieuphim.util.StringUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@Order(1) // Chạy đầu tiên
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserInitializer implements ApplicationRunner {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Kiểm tra và khởi tạo dữ liệu User...");

        String defaultPassword = passwordEncoder.encode("12345678");
        String adminPassword = passwordEncoder.encode("admin123");

        List<User> usersToSave = new ArrayList<>();

        // 1. Tài khoản Admin
        if (!userRepository.existsByUsername("admin")) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(adminPassword);
            admin.setFullName("Quản trị hệ thống");
            admin.setEmail("admin@rapchieuphim.com");
            admin.setPhoneNumber("0900000000");
            usersToSave.add(admin);
        }

        // 2. Tài khoản Nhân sự (Manager & SaleStaff)
        if (!userRepository.existsByUsername("manager1")) {
            Manager manager1 = new Manager();
            manager1.setUsername("manager1");
            manager1.setPassword(defaultPassword);
            manager1.setFullName("Trần Quản Lý");
            usersToSave.add(manager1);
        }
        if (!userRepository.existsByUsername("manager2")) {
            Manager manager2 = new Manager();
            manager2.setUsername("manager2");
            manager2.setPassword(defaultPassword);
            manager2.setFullName("Lê Điều Hành");
            usersToSave.add(manager2);
        }
        if (!userRepository.existsByUsername("sale1")) {
            SaleStaff sale1 = new SaleStaff();
            sale1.setUsername("sale1");
            sale1.setPassword(defaultPassword);
            sale1.setFullName("Nguyễn Nhân Viên");
            usersToSave.add(sale1);
        }

        // 3. 60 tài khoản Khách hàng thành viên
        String[] fullNames = {
                "Nguyễn Tuấn Anh", "Trần Thị Bích", "Lê Hoàng Cường", "Phạm Tiến Dũng", "Hoàng Ngọc Ánh",
                "Đỗ Gia Bảo", "Vũ Thu Giang", "Bùi Thanh Hải", "Nguyễn Minh Huy", "Trần Quốc Khang",
                "Lê Mỹ Linh", "Phạm Nam Phong", "Hoàng Lan Phương", "Đỗ Quang Sang", "Vũ Minh Tâm",
                "Bùi Anh Tuấn", "Nguyễn Nhã Uyên", "Trần Thế Vinh", "Lê Thảo Vy", "Phạm Hải Yến",
                "Hoàng Văn Kiên", "Đỗ Mai Lan", "Vũ Nhật Minh", "Bùi Ngọc Nga", "Nguyễn Hữu Phúc",
                "Trần Thanh Quyên", "Lê Minh Sáng", "Phạm Thị Sen", "Hoàng Công Thành", "Đỗ Minh Trí",
                "Vũ Cát Tường", "Bùi Công Xuân", "Nguyễn Đình Trọng", "Trần Quang Hải", "Lê Văn Toàn",
                "Phạm Xuân Trường", "Hoàng Tuấn Anh", "Đỗ Hùng Dũng", "Vũ Văn Thanh", "Bùi Tiến Dũng",
                "Nguyễn Quang Vinh", "Trần Bảo Ngọc", "Lê Đức Thịnh", "Phạm Khánh Linh", "Hoàng Minh Khôi",
                "Đỗ Thùy Trang", "Vũ Quốc Đạt", "Bùi Phương Thảo", "Nguyễn Trọng Hoàng", "Trần Minh Vương",
                "Lê Thành Chung", "Phạm Tuấn Hải", "Hoàng Việt Anh", "Đỗ Duy Mạnh", "Vũ Tiến Long",
                "Bùi Hoàng Việt", "Nguyễn Thanh Bình", "Trần Nguyên Mạnh", "Lê Ngọc Bảo", "Phạm Đức Huy"
        };

        Random random = new Random();

        for (int i = 0; i < fullNames.length; i++) {
            String fullName = fullNames[i];
            String username = generateUsername(fullName);

            if (!userRepository.existsByUsername(username)) {
                Member member = new Member();
                member.setUsername(username);
                member.setPassword(defaultPassword);
                member.setFullName(fullName);
                member.setEmail(username + "@gmail.com");
                member.setPhoneNumber("0930" + String.format("%06d", i + 1));
                member.setDate(LocalDate.of(1990 + random.nextInt(15), (i % 12) + 1, (i % 28) + 1));
                member.setAddress("Hà Nội");

                // Khởi tạo tất cả Member ở vạch xuất phát: 0 điểm - Hạng Đồng (BRONZE)
                member.setMemberRank(MemberRank.BRONZE);
                member.setMemberPoint(0);

                usersToSave.add(member);
            }
        }

        if (!usersToSave.isEmpty()) {
            userRepository.saveAll(usersToSave);
            log.info("Đã khởi tạo thêm {} User mới.", usersToSave.size());
        } else {
            log.info("Dữ liệu User đã đầy đủ, không cần tạo thêm.");
        }
    }

    // Hàm tự động tạo username: Tên_(họ đệm viết tắt)_123
    private String generateUsername(String fullName) {
        String cleanName = StringUtils.removeAccents(fullName).toLowerCase();
        String[] parts = cleanName.split("\\s+");
        if (parts.length == 1) return parts[0] + "_123";
        String ten = parts[parts.length - 1];
        StringBuilder hoDem = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            hoDem.append(parts[i].charAt(0));
        }
        return ten + "_" + hoDem.toString() + "_123";
    }
}