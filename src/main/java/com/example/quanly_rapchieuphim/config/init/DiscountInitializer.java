package com.example.quanly_rapchieuphim.config.init;

import com.example.quanly_rapchieuphim.entity.Discount;
import com.example.quanly_rapchieuphim.enums.DiscountScope;
import com.example.quanly_rapchieuphim.enums.DiscountType;
import com.example.quanly_rapchieuphim.repository.DiscountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Order(3) // Chạy thứ ba
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DiscountInitializer implements ApplicationRunner {

    DiscountRepository discountRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Kiểm tra và khởi tạo dữ liệu Discount...");
        List<Discount> discountsToSave = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        addDiscountIfNotExist(discountsToSave, "WELCOME20", "Khuyến mãi chào bạn mới", DiscountType.PERCENTAGE, DiscountScope.INVOICE, 20.0, 50000.0, 100000.0, now.minusDays(1), now.plusMonths(1));
        addDiscountIfNotExist(discountsToSave, "TICKET50K", "Giảm trực tiếp 50K tiền vé", DiscountType.FIXED_AMOUNT, DiscountScope.TICKET, 50000.0, 50000.0, 150000.0, now, now.plusMonths(2));
        addDiscountIfNotExist(discountsToSave, "FREEPOPCORN", "Tặng 1 Bắp/Nước", DiscountType.PERCENTAGE, DiscountScope.PRODUCT, 100.0, 50000.0, 0.0, now, now.plusDays(15));
        addDiscountIfNotExist(discountsToSave, "HSSV10", "Ưu đãi Học Sinh Sinh Viên", DiscountType.PERCENTAGE, DiscountScope.INVOICE, 10.0, 500000.0, 0.0, now.minusMonths(1), now.plusYears(1));
        addDiscountIfNotExist(discountsToSave, "FLASH30K", "Flash Sale Cuối Tuần", DiscountType.FIXED_AMOUNT, DiscountScope.INVOICE, 30000.0, 30000.0, 200000.0, now, now.plusDays(3));

        if (!discountsToSave.isEmpty()) {
            discountRepository.saveAll(discountsToSave);
            log.info("Đã tạo mới {} Discount.", discountsToSave.size());
        } else {
            log.info("Dữ liệu Discount đã đầy đủ.");
        }
    }

    private void addDiscountIfNotExist(List<Discount> list, String code, String name, DiscountType type, DiscountScope scope, double value, double maxDiscount, double minValue, LocalDateTime start, LocalDateTime end) {
        if (!discountRepository.existsByCode(code)) {
            list.add(Discount.builder().code(code).name(name).type(type).scope(scope).value(value)
                    .maxDiscount(maxDiscount).minValue(minValue).startDate(start).endDate(end).build());
        }
    }
}