package com.example.quanly_rapchieuphim.config.init;

import com.example.quanly_rapchieuphim.entity.Product;
import com.example.quanly_rapchieuphim.enums.ProductType;
import com.example.quanly_rapchieuphim.enums.Status;
import com.example.quanly_rapchieuphim.repository.ProductRepository;
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
@Order(2) // Chạy thứ hai
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductInitializer implements ApplicationRunner {

    ProductRepository productRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Kiểm tra và khởi tạo dữ liệu Product...");
        List<Product> productsToSave = new ArrayList<>();

        addProductIfNotExist(productsToSave, "Bắp rang bơ (Ngọt)", ProductType.FOOD, 'M', 45000.0);
        addProductIfNotExist(productsToSave, "Bắp rang bơ (Ngọt)", ProductType.FOOD, 'L', 55000.0);
        addProductIfNotExist(productsToSave, "Bắp rang phô mai", ProductType.FOOD, 'M', 50000.0);
        addProductIfNotExist(productsToSave, "Bắp rang phô mai", ProductType.FOOD, 'L', 60000.0);
        addProductIfNotExist(productsToSave, "Coca Cola", ProductType.DRINK, 'M', 30000.0);
        addProductIfNotExist(productsToSave, "Coca Cola", ProductType.DRINK, 'L', 40000.0);
        addProductIfNotExist(productsToSave, "Sprite", ProductType.DRINK, 'M', 30000.0);
        addProductIfNotExist(productsToSave, "Nước suối Dasani", ProductType.DRINK, 'S', 20000.0);
        addProductIfNotExist(productsToSave, "Combo Solo", ProductType.COMBO, 'M', 70000.0);
        addProductIfNotExist(productsToSave, "Combo Couple", ProductType.COMBO, 'L', 120000.0);

        if (!productsToSave.isEmpty()) {
            productRepository.saveAll(productsToSave);
            log.info("Đã tạo mới {} Product.", productsToSave.size());
        } else {
            log.info("Dữ liệu Product đã đầy đủ.");
        }
    }

    private void addProductIfNotExist(List<Product> list, String name, ProductType type, char size, double price) {
        if (!productRepository.existsByNameAndSize(name, size)) {
            list.add(Product.builder().name(name).type(type).size(size).price(price).status(Status.ENABLE).build());
        }
    }
}