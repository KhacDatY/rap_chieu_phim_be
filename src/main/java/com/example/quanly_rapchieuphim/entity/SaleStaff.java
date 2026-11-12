package com.example.quanly_rapchieuphim.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sale_staffs")
@DiscriminatorValue("SALE_STAFF")
@Getter
@Setter
@NoArgsConstructor
public class SaleStaff extends User {
}