package mini.project.HotelReservation.DiscountPolicy.DaysDiscountPolicy;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class DaysFixDiscountPolicy implements DaysDiscountPolicy{
    int discountPrice = 30000;
    @Override
    public int discount(int noDiscountPrice, int days) {
        return noDiscountPrice >= 100000 ? discountPrice * (days / 3) : 0;
        // 리턴 = 공제 금액
    }
}