package com.example.quanly_rapchieuphim.entity;

import com.example.quanly_rapchieuphim.enums.MemberRank;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "members")
@DiscriminatorValue("MEMBER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member extends User {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRank memberRank;

    @Column(nullable = false)
    private Integer memberPoint;
}