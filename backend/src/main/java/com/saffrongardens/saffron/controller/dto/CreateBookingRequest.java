package com.saffrongardens.saffron.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CreateBookingRequest {
    private Long hallId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal totalAmount;
    private List<VendorAssignmentRequest> vendorAssignments;

    public Long getHallId() { return hallId; }
    public void setHallId(Long hallId) { this.hallId = hallId; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public List<VendorAssignmentRequest> getVendorAssignments() { return vendorAssignments; }
    public void setVendorAssignments(List<VendorAssignmentRequest> vendorAssignments) { this.vendorAssignments = vendorAssignments; }
}
