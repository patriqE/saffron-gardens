package com.saffrongardens.saffron.repository;

import com.saffrongardens.saffron.entity.Booking;
import com.saffrongardens.saffron.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking b where b.status = :status and b.startTime < :end and b.endTime > :start")
    List<Booking> findConflicting(@Param("status") BookingStatus status, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
