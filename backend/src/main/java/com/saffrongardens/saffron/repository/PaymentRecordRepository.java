package com.saffrongardens.saffron.repository;

import com.saffrongardens.saffron.entity.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {

	java.util.List<PaymentRecord> findByBookingId(Long bookingId);

}
