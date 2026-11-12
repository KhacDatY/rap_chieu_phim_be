package com.example.quanly_rapchieuphim.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "managers")
@DiscriminatorValue("MANAGER")
@Getter
@Setter
@NoArgsConstructor
public class Manager extends User {
}