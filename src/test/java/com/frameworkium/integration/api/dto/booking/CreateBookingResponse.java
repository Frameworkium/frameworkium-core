package com.frameworkium.integration.api.dto.booking;

import com.frameworkium.core.api.dto.AbstractDTO;

public class CreateBookingResponse extends AbstractDTO<CreateBookingResponse> {
    public Booking booking;
    public int bookingid;
}
