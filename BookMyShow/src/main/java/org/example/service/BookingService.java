package org.example.service;

import org.example.constants.BookingStatus;
import org.example.constants.PaymentStatus;
import org.example.constants.SeatStatus;
import org.example.model.*;
import org.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SeatLockingService seatLockingService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ShowSeatRepository showSeatRepository;

    @Autowired
    private BookingItemRepository bookingItemRepository;

    Booking createBooking(Long userId, Long showId, List<Long> seatIds, String paymentMethod) {
        for(Long seatId : seatIds) {
            boolean locked = seatLockingService.tryLockSeat(showSeatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException(String.format("Seat not found: %d", seatId))));
            if(!locked)
                throw new RuntimeException(String.format("Seat %d is already locked/booked", seatId));
        }

        for(Long seatId : seatIds) {
            ShowSeat seat = showSeatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException(String.format("Show seat not found: %d", seatId)));
            BookingItem bookingItem = new BookingItem();
            bookingItem.setShowSeat(seat);
            bookingItem.setPrice(seat.getPrice());
            bookingItemRepository.save(bookingItem);

            seat.setStatus(SeatStatus.LOCKED);
            showSeatRepository.save(seat);
        }

        final Booking booking = new Booking();
        booking.setRef(UUID.randomUUID());
        booking.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));
        booking.setStatus(BookingStatus.PENDING);
        bookingRepository.save(booking);

        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);
        payment.setBooking(booking);
        payment.setProvider(paymentMethod);
        paymentRepository.save(payment);

        return booking;
    }

    void confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException(String.format("Invalid booking id: %d", bookingId)));
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        for(var item : booking.getBookingItems()) {
            ShowSeat seat = item.getShowSeat();
            seat.setStatus(SeatStatus.BOOKED);
            showSeatRepository.save(seat);
            seatLockingService.releaseLock(seat);
        }

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException(String.format("Payment not found for bookingId: %d", bookingId)));
        payment.setStatus(PaymentStatus.SUCCESS);
    }

    void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException(String.format("Invalid booking id: %d", bookingId)));
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        for(var item : booking.getBookingItems()) {
            ShowSeat seat = item.getShowSeat();
            seat.setStatus(SeatStatus.AVAILABLE);
            showSeatRepository.save(seat);
            seatLockingService.releaseLock(seat);
        }

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException(String.format("Payment not found for bookingId: %d", bookingId)));
        payment.setStatus(PaymentStatus.CANCELLED);
    }
}
