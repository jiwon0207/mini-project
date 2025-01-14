package mini.project.HotelReservation.Host.Controller;

import lombok.RequiredArgsConstructor;
import mini.project.HotelReservation.Host.Data.Dto.HotelReservationDto;
import mini.project.HotelReservation.Host.Data.Dto.PriceDto;
import mini.project.HotelReservation.Host.Data.Dto.RoomStockDto;
import mini.project.HotelReservation.Host.Service.HostService;
import mini.project.HotelReservation.enumerate.DiscountPolicy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/host")
public class HostController {
    private final HostService hostService;

    @GetMapping("")
    String manage(Model model){
        // TODO: 호스트의 메인 페이지 로직을 구현 (호텔 관련 정보를 모델에 담아서 반환)
        model.addAttribute("asd");
        return "redirect:/host/manage";
    }
    @PatchMapping("/policy")
    String discountPolicy(String policy){
        // TODO: 할인 정책 변경 로직 구현
        hostService.changePolicy(DiscountPolicy.valueOf(policy));
            // Exception 처리할 것 (IllegalArgumentException)
        return "redirect:/host";
    }
    @PatchMapping("/price")
    String roomPrice(PriceDto priceDto){
        // TODO: 객실 가격 변경 로직 구현
        hostService.modifyRoomPrice(priceDto);
            // Exception 처리할 것 (IllegalArgumentException)
        return "redirect:/host";
    }
    @PatchMapping("/stock")
    String roomStock(RoomStockDto roomStockDto){
        // TODO: 객실 재고 변경 로직 구현
        hostService.modifyRoomStock(roomStockDto);
            // Exception 처리할 것 (IllegalArgumentException)
        return "redirect:/host";
    }
    @GetMapping("/reservations")
    String reserveAll(Model model){
        // TODO: 호스트의 예약 목록 페이지 로직 구현 (예약 목록을 모델에 담아서 반환)
        List<HotelReservationDto> reservations = hostService.reservationList();
        model.addAttribute("reservations", reservations);
        return "redirect:/host/reservations";
    }

    @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class})
    public String handel(Exception e, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("Error", "다시 입력해주세요.");
        return "redirect:/host";
    }
}
