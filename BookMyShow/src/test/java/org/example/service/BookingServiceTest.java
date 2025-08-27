package org.example.service;

import org.example.constants.BookingStatus;
import org.example.constants.PaymentStatus;
import org.example.constants.SeatStatus;
import org.example.model.*;
import org.example.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SeatLockingService seatLockingService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ShowSeatRepository showSeatRepository;

    @Mock
    private BookingItemRepository bookingItemRepository;

    @InjectMocks
    private BookingService bookingService;

    private User user;
    private Booking booking;
    private Payment payment;
    private ShowSeat seat;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setName("GG");

        seat = new ShowSeat();
        seat.setId(1L);
        seat.setStatus(SeatStatus.AVAILABLE);
        seat.setPrice(10.0);

        booking = new Booking();
        booking.setUser(user);
        booking.setId(1L);
        booking.setStatus(BookingStatus.PENDING);
        booking.setBookingItems(new ArrayList<>());

        payment = new Payment();
        payment.setId(1L);
        payment.setBooking(booking);
        payment.setStatus(PaymentStatus.PENDING);
    }

    @Test
    public void testCreateBooking_Success() {
        List<Long> seatIds = List.of(seat.getId());

        Mockito.when(showSeatRepository.findById(seat.getId())).thenReturn(Optional.of(seat));
        Mockito.when(seatLockingService.tryLockSeat(seat)).thenReturn(true);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(booking);
        Mockito.when(paymentRepository.save(Mockito.any(Payment.class))).thenReturn(payment);
        Mockito.when(bookingItemRepository.save(Mockito.any(BookingItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingService.createBooking(user.getId(), 1L, seatIds, "UPI");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(BookingStatus.PENDING, result.getStatus());
        Mockito.verify(bookingRepository, Mockito.times(1)).save(Mockito.any(Booking.class));
        Mockito.verify(paymentRepository, Mockito.times(1)).save(Mockito.any(Payment.class));
        Mockito.verify(seatLockingService, Mockito.times(1)).tryLockSeat(seat);
    }

    @Test
    public void testCreateBooking_SeatAlreadyLocked() {
        Mockito.when(showSeatRepository.findById(seat.getId())).thenReturn(Optional.of(seat));
        Mockito.when(seatLockingService.tryLockSeat(seat)).thenReturn(false);

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () ->
                bookingService.createBooking(user.getId(), 1L, List.of(seat.getId()), "UPI"));

        Assertions.assertTrue(ex.getMessage().contains("already locked/booked"));
    }

    // ----------------- confirmBooking -----------------

    @Test
    public void testConfirmBooking_Success() {
        BookingItem item = new BookingItem();
        item.setId(200L);
        item.setShowSeat(seat);
        booking.getBookingItems().add(item);

        Mockito.when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        Mockito.when(paymentRepository.findByBookingId(booking.getId())).thenReturn(Optional.of(payment));

        bookingService.confirmBooking(booking.getId());

        Assertions.assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        Assertions.assertEquals(SeatStatus.BOOKED, seat.getStatus());
        Assertions.assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        Mockito.verify(seatLockingService, Mockito.times(1)).releaseLock(seat);
    }

    @Test
    public void testConfirmBooking_NotFound() {
        Mockito.when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () ->
                bookingService.confirmBooking(999L));

        Assertions.assertTrue(ex.getMessage().contains("Invalid booking id"));
    }

    // ----------------- cancelBooking -----------------

    @Test
    public void testCancelBooking_Success() {
        BookingItem item = new BookingItem();
        item.setId(201L);
        item.setShowSeat(seat);
        booking.getBookingItems().add(item);

        Mockito.when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        Mockito.when(paymentRepository.findByBookingId(booking.getId())).thenReturn(Optional.of(payment));

        bookingService.cancelBooking(booking.getId());

        Assertions.assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        Assertions.assertEquals(SeatStatus.AVAILABLE, seat.getStatus());
        Assertions.assertEquals(PaymentStatus.CANCELLED, payment.getStatus());
        Mockito.verify(seatLockingService, Mockito.times(1)).releaseLock(seat);
    }

    @Test
    public void testCancelBooking_InvalidBookingId() {
        Mockito.when(bookingRepository.findById(123L)).thenReturn(Optional.empty());

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () ->
                bookingService.cancelBooking(123L));

        Assertions.assertTrue(ex.getMessage().contains("Invalid booking id"));
    }
}
