package com.frameworkium.integration.heroku.restfulbooker.api.tests;

import com.frameworkium.core.api.tests.BaseTest;
import com.frameworkium.integration.heroku.restfulbooker.api.dto.booking.*;
import com.frameworkium.integration.heroku.restfulbooker.api.service.booking.BookingService;
import com.frameworkium.integration.heroku.restfulbooker.api.service.ping.PingService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.assertThat;

public class BookerTest extends BaseTest {

    @BeforeClass
    public void ensure_site_is_up_by_using_ping_service() {
        assertThat(new PingService().ping())
                .isEqualTo("Created");
    }

    @Test
    public void create_new_booking() {
        BookingService service = new BookingService();
        Booking booking = Booking.newInstance();

        CreateBookingResponse bookingResponse = service.createBooking(booking);
        assertThat(bookingResponse.booking).isEqualTo(booking);
        assertThat(service.getBooking(bookingResponse.bookingid))
                .isEqualTo(booking);
    }

    @Test
    public void auth_token_matches_expected_pattern() {
        String token = new BookingService().createAuthToken(
                "admin", "password123");
        assertThat(token).matches("[a-z0-9]{15}");
    }

    @Test
    public void delete_newly_created_booking() {
        // create booking
        BookingService service = new BookingService();
        Booking booking = Booking.newInstance();
        int bookingID = service.createBooking(booking).bookingid;

        String token = new BookingService().createAuthToken(
                "admin", "password123");
        service.delete(bookingID, token);
        assertThat(service.doesBookingExist(bookingID)).isFalse();
        assertThat(service.listBookings())
                .doesNotContain(BookingID.of(bookingID));
    }

}
