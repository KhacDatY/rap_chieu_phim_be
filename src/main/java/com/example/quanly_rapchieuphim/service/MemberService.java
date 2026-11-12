package com.example.quanly_rapchieuphim.service;

import com.example.quanly_rapchieuphim.entity.Member;
import com.example.quanly_rapchieuphim.enums.MemberRank;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MemberService {

    /**
     * Cộng điểm tích lũy dựa trên tổng tiền thanh toán và tự động cập nhật hạng thành viên
     * Quy tắc: 1,000 VNĐ = 1 điểm (VD: Hóa đơn 150,000 VNĐ -> cộng 150 điểm)
     */
    public void addPointsAndUpgradeRank(Member member, Double totalAmountPaid) {
        if (member == null || totalAmountPaid == null || totalAmountPaid <= 0) {
            return;
        }

        int earnedPoints = (int) (totalAmountPaid / 1000);
        int currentPoints = member.getMemberPoint() != null ? member.getMemberPoint() : 0;
        int newTotalPoints = currentPoints + earnedPoints;

        member.setMemberPoint(newTotalPoints);

        MemberRank newRank = calculateRank(newTotalPoints);
        member.setMemberRank(newRank);
    }

    /**
     * Xác định hạng thành viên tương ứng với mốc điểm
     */
    public MemberRank calculateRank(int points) {
        if (points >= 5000) {
            return MemberRank.DIAMOND;
        } else if (points >= 3000) {
            return MemberRank.PLATINUM;
        } else if (points >= 1500) {
            return MemberRank.GOLD;
        } else if (points >= 500) {
            return MemberRank.SILVER;
        } else {
            return MemberRank.BRONZE;
        }
    }
}