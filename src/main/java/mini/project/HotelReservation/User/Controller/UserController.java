package mini.project.HotelReservation.User.Controller;

import lombok.RequiredArgsConstructor;
import mini.project.HotelReservation.Reservation.Data.Dto.ReservationRequestDto;
import mini.project.HotelReservation.Reservation.Data.Dto.ReservationResponseDto;
import mini.project.HotelReservation.Reservation.Repository.ReservationRepository;
import mini.project.HotelReservation.User.Data.Dto.UserInfoDto;
import mini.project.HotelReservation.User.Data.Dto.UserReservationDto;
import mini.project.HotelReservation.User.Data.Dto.UserSignInDto;
import mini.project.HotelReservation.User.Data.Dto.UserSignUpDto;
import mini.project.HotelReservation.User.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    // 로그인 화면(시작)
    @GetMapping("/in")
    public String getLogIn(Model model){
        model.addAttribute("userSignInDto",new UserSignInDto());
        return "user/login";
    }

    //로그인
    @PostMapping("/in")
    public String postLogIn(@ModelAttribute("userSignInDto") UserSignInDto userSignInDto){
        System.out.println(userSignInDto.getEmail());
        userService.logIn(userSignInDto);
        return "reservation/main";
    }

    //회원가입 화면
    @GetMapping("/new")
    public String  getJoin(Model model){
        model.addAttribute("userSignUpDto",new UserSignUpDto());
        return "user/join";
    }

    //회원가입
    @PostMapping("/new")
    public String postJoin(@ModelAttribute("userSignUpDto") UserSignUpDto userSignUpDto){
        userService.join(userSignUpDto);
        return "user/login";
    }

    // 로그아웃
    @GetMapping(value = "/out")
    public String getLogOut(){
        return "redirect:/logout";
    }

    // 유저측 예약리스트 조회
    @GetMapping(value = "/reservations")
    public String getUserReservationList(Model model){
        model.addAttribute("userReservationDtoList", userService.reservationList());
        return "user/userReservationList";
    }

    //todo:{rN}? & html에 값 잘 들어가는지?
    @GetMapping(value = "/reservation/{reserveNumber}")
    public String getUserReservation(Model model, @RequestParam("reservationResponseDto") ReservationResponseDto reservationResponseDto) {
        return "user/join";
    }

    @GetMapping("")
    public String getUserInfo(Model model){
        model.addAttribute("userInfoDto", new UserInfoDto("","","",""));
        return "user/userInfo";
    }

    @PutMapping
    public String putUserInfo(@RequestParam("user") UserInfoDto user){

        return "user/join";
    }

    @PatchMapping
    public String quit(Model model, RedirectAttributes redirectAttributes){
        return "user/join";
    }
}