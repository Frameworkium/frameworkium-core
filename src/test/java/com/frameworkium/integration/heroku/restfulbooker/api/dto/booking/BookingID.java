package com.frameworkium.integration.heroku.restfulbooker.api.dto.booking;

import com.frameworkium.core.api.dto.AbstractDTO;

public class BookingID extends AbstractDTO<BookingID> {

    public int bookingid;

    public static BookingID of(int bookingID) {
        BookingID id = new BookingID();
        id.bookingid = bookingID;
        return id;
    }
}
