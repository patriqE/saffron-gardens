package com.saffrongardens.saffron.service;

import com.saffrongardens.saffron.entity.Hall;
import com.saffrongardens.saffron.entity.BookingStatus;
import com.saffrongardens.saffron.repository.BookingRepository;
import com.saffrongardens.saffron.repository.HallRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AvailabilityService {

    private final BookingRepository bookingRepository;
    private final HallRepository hallRepository;

    public AvailabilityService(BookingRepository bookingRepository, HallRepository hallRepository) {
        this.bookingRepository = bookingRepository;
        this.hallRepository = hallRepository;
    }

    public List<Hall> findAvailable(LocalDateTime start, LocalDateTime end) {
        // find all confirmed bookings that overlap
        List<com.saffrongardens.saffron.entity.Booking> conflicts = bookingRepository.findConflicting(BookingStatus.CONFIRMED, start, end);
        Set<Long> bookedHallIds = conflicts.stream().map(b -> b.getHall().getId()).collect(Collectors.toSet());
        if (bookedHallIds.isEmpty()) return hallRepository.findAll();
        return hallRepository.findAll().stream().filter(h -> !bookedHallIds.contains(h.getId())).collect(Collectors.toList());
    }

    public boolean isAvailable(Long hallId, LocalDateTime start, LocalDateTime end) {
        List<com.saffrongardens.saffron.entity.Booking> conflicts = bookingRepository.findConflicting(BookingStatus.CONFIRMED, start, end);
        return conflicts.stream().noneMatch(b -> b.getHall().getId().equals(hallId));
    }
}
