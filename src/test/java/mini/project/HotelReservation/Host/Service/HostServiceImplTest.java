package mini.project.HotelReservation.Host.Service;

import jakarta.servlet.http.HttpServletRequest;
import mini.project.HotelReservation.Configure.Seucurity.TokenDecoder;
import mini.project.HotelReservation.Host.Data.Dto.HotelReservationDto;
import mini.project.HotelReservation.Host.Data.Dto.PriceDto;
import mini.project.HotelReservation.Host.Data.Dto.RoomStockDto;
import mini.project.HotelReservation.Host.Data.Entity.Hotel;
import mini.project.HotelReservation.Host.Data.Entity.Room;
import mini.project.HotelReservation.Host.Repository.HotelRepository;
import mini.project.HotelReservation.Host.Repository.RoomRepository;
import mini.project.HotelReservation.Reservation.Data.Entity.Reservation;
import mini.project.HotelReservation.Reservation.Repository.ReservationRepository;
import mini.project.HotelReservation.User.Data.Entity.User;
import mini.project.HotelReservation.User.Repository.UserRepository;
import mini.project.HotelReservation.enumerate.DiscountPolicy;
import mini.project.HotelReservation.enumerate.RoomType;
import mini.project.HotelReservation.enumerate.UserRole;
import mini.project.HotelReservation.enumerate.UserStatus;
import mockit.Mocked;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class HostServiceImplTest {
    @Autowired
    private HostService hostService;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenDecoder td;

    @Mocked
    HttpServletRequest mockRequest;

    // 초기화
    @BeforeEach
    void init(){
        // 토큰 초기화 및 더미 요청 생성
        td.init();
        mockRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // 호스트 생성
        User host = new User("Hotel_A",
                "abc@example.com",
                "1234",
                "010-1234-5678",
                UserStatus.USER_STATUS_ACTIVE,
                UserRole.ROLE_HOST);
        // 호텔 생성
        // A
        Hotel hotel = new Hotel("성북구",
                "Hotel_A",
                "02-123-4567",
                DiscountPolicy.POLICY_PEAK,
                LocalTime.of(13, 0, 0),
                LocalTime.of(10, 0, 0),
                LocalDate.now(),
                LocalDate.now().plusMonths(2));
        hotel.foreignUser(host);
        Hotel saveHotel = hotelRepository.save(hotel);
        // B
        Hotel hotelB = new Hotel("신대방",
                "Hotel_B",
                "02-123-4567",
                DiscountPolicy.POLICY_PEAK,
                LocalTime.of(13, 0, 0),
                LocalTime.of(18, 0, 0),
                LocalDate.now(),
                LocalDate.now().plusMonths(2));
        // 귀찮아서 얘는 호스트 없음
        Hotel saveHotelB = hotelRepository.save(hotelB);

        // 객실 생성
            // 호텔 A꺼
        Room roomA = new Room(RoomType.ROOM_TYPE_A_SINGLE, 100000, 10);
        roomA.foreignHotel(saveHotel);
        Room roomB = new Room(RoomType.ROOM_TYPE_B_TWIN, 200000, 20);
        roomB.foreignHotel(saveHotel);
            // 호텔 B꺼
        Room roomC = new Room(RoomType.ROOM_TYPE_C_QUEEN, 300000, 20);
        roomC.foreignHotel(saveHotelB);
        roomRepository.saveAll(List.of(roomA,roomB, roomC));
        // 예약 1, 2, 3 생성
        Reservation reservation1 = new Reservation("AA1-230523",
                3000000, RoomType.ROOM_TYPE_A_SINGLE, "Hotel_A"
                ,"010-2222-3333", "Serah",
                LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(5).atStartOfDay());
        reservation1.foreignUser(host);  reservation1.foreignHotel(hotel);
        Reservation reservation2 = new Reservation("AB1-430525",
                5400000, RoomType.ROOM_TYPE_B_TWIN, "Hotel_A"
                ,"010-4444-5555", "Grima",
                LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(5).atStartOfDay());
        reservation2.foreignUser(host);  reservation2.foreignHotel(hotel);
        Reservation reservation3 = new Reservation("BC1-630528",
                50034600, RoomType.ROOM_TYPE_C_QUEEN, "Hotel_B"
                ,"010-6666-7777", "Mosquito",
                LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(5).atStartOfDay());
        reservation3.foreignUser(host);  reservation3.foreignHotel(hotelB);
        reservationRepository.saveAll(List.of(reservation1, reservation2, reservation3));

        User ckUser = userRepository.findById(userRepository.findAll().get(0).getUserId()).get();
        td.createToken(String.valueOf(ckUser.getRole()), String.valueOf(ckUser.getUserId()), String.valueOf(ckUser.getHotel().getHotelId()));
        SecurityContextHolder.getContext().setAuthentication(td.getAuthentication(td.resolveToken(mockRequest)));
    }
    @AfterEach
    void reset(){
        reservationRepository.deleteAll();
        roomRepository.deleteAll();
        userRepository.deleteAll();
        hotelRepository.deleteAll();
    }

    @Test
    @DisplayName("호스트_정책_변경")
    void change_Policy() {
        // 저장된 유저에서 연결된 호텔 받아오기
        hostService.changePolicy(DiscountPolicy.POLICY_ALL);
        assertEquals(td.currentUser().getHotel().getDiscountPolicy(), DiscountPolicy.POLICY_ALL);
    }
    @Test
    @DisplayName("방가격_변경")
    void modifyRoomPrice() {
        PriceDto priceDto = new PriceDto(RoomType.ROOM_TYPE_A_SINGLE, 500);
        hostService.modifyRoomPrice(priceDto);
        assertEquals(roomRepository.findById(roomRepository.findAll().get(0).getRoomId()).get().getRoomPrice(), 500);
    }
    @Test
    @DisplayName("방개수_변경")
    void modifyRoomStock() {
        RoomStockDto stockDto = new RoomStockDto(RoomType.ROOM_TYPE_A_SINGLE, 50);
        hostService.modifyRoomStock(stockDto);
        assertEquals(roomRepository.findByHotelNameAndRoomType(td.currentUser().getName(), RoomType.ROOM_TYPE_A_SINGLE).getRoomStock(), 50);
    }
    @Test
    @DisplayName("호텔측_예약리스트_보기")
    void reservationList() {
        List<HotelReservationDto> reservations = hostService.reservationList();
        assertEquals(reservations.size(),
                reservationRepository.findAllByHotel_HotelId(
                        userRepository.findById(userRepository.findAll().get(0).getUserId()).get().getHotel().getHotelId()
                ).size());
    }
}