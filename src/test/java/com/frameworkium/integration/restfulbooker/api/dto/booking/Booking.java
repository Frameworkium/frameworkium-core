package com.frameworkium.integration.restfulbooker.api.dto.booking;

import com.frameworkium.lite.api.dto.AbstractDTO;

import java.util.concurrent.ThreadLocalRandom;

public class Booking extends AbstractDTO<Booking> {
    public String firstname;
    public String lastname;
    public long totalprice;
    public boolean depositpaid;
    public BookingDates bookingdates;
    public String additionalneeds;

    public static Booking newInstance() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int randInt = random.nextInt();
        Booking booking = new Booking();
        booking.firstname = "firstname" + randInt;
        booking.lastname = "lastname" + randInt;
        booking.totalprice = randInt;
        booking.depositpaid = random.nextBoolean();
        booking.bookingdates = BookingDates.newInstance();
        booking.additionalneeds = null;
        return booking;
    }
}

