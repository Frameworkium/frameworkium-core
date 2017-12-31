package com.frameworkium.integration.heroku.restfulbooker.api.tests;

import com.frameworkium.core.api.tests.BaseTest;
import com.frameworkium.integration.heroku.restfulbooker.api.dto.booking.*;
import com.frameworkium.integration.heroku.restfulbooker.api.service.booking.BookingService;
import com.frameworkium.integration.heroku.restfulbooker.api.service.ping.PingService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

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

        BookingResponse bookingResponse = service.createBooking(booking);
        assertThat(bookingResponse.booking).isEqualTo(booking);
        // also check it persisted, but need to search for booking ID
        assertThat(service.getBooking(searchBookingIDByName(booking)))
                .isEqualTo(booking);
    }

    private int searchBookingIDByName(Booking booking) {
        Map<String, String> searchParams = SearchParamsMapper.namesOfBooking(booking);
        List<BookingID> bookingIDs =
                new BookingService().search(searchParams);
        assertThat(bookingIDs.size()).isEqualTo(1);
        return bookingIDs.get(0).bookingid;
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

        service.createBooking(booking);
        int bookingID = searchBookingIDByName(booking);
        String token = new BookingService().createAuthToken(
                "admin", "password123");
        service.delete(bookingID, token);
        assertThat(service.doesBookingExist(bookingID)).isFalse();
        assertThat(service.listBookings())
                .doesNotContain(BookingID.of(bookingID));
    }

}
