package com.saffrongardens.saffron.controller.dto;

import java.math.BigDecimal;

public class VendorAssignmentRequest {
    private Long vendorId;
    private String vendorType;
    private String notes;
    private BigDecimal price;

    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    public String getVendorType() { return vendorType; }
    public void setVendorType(String vendorType) { this.vendorType = vendorType; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
