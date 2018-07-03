package com.frameworkium.integration.heroku.restfulbooker.api.dto.booking;

import com.frameworkium.core.api.dto.AbstractDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BookingDates extends AbstractDTO<BookingDates> {

    public static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ISO_LOCAL_DATE;

    public String checkin;
    public String checkout;

    public static BookingDates newInstance() {
        BookingDates dates = new BookingDates();
        dates.checkin = LocalDate.now().plusDays(1).format(FORMAT);
        dates.checkout = LocalDate.now().plusDays(10).format(FORMAT);
        return dates;
    }
}
