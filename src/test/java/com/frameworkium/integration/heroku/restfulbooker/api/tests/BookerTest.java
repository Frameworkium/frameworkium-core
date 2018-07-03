package com.frameworkium.integration.heroku.restfulbooker.api.tests;

import com.frameworkium.core.api.tests.BaseAPITest;
import com.frameworkium.core.common.retry.RetryFlakyTest;
import com.frameworkium.integration.heroku.restfulbooker.api.dto.booking.*;
import com.frameworkium.integration.heroku.restfulbooker.api.service.booking.BookingService;
import com.frameworkium.integration.heroku.restfulbooker.api.service.ping.PingService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.assertThat;

// app resets every 10m, so could happen in the middle of this test
@Test(retryAnalyzer = RetryFlakyTest.class)
public class BookerTest extends BaseAPITest {

    @BeforeClass
    public void ensure_site_is_up_by_using_ping_service() {
        assertThat(new PingService().ping())
                .isEqualTo("Created");
    }

    public void create_new_booking() {
        // given some booking data
        BookingService service = new BookingService();
        Booking booking = Booking.newInstance();

        // when creating the booking
        CreateBookingResponse bookingResponse = service.createBooking(booking);

        // the booking returned matches the input and is persisted
        assertThat(bookingResponse.booking)
                .isEqualTo(booking);
        assertThat(service.getBooking(bookingResponse.bookingid))
                .isEqualTo(booking);
    }

    public void auth_token_matches_expected_pattern() {
        String token = new BookingService().createAuthToken(
                "admin", "password123");
        assertThat(token).matches("[a-z0-9]{15}");
    }

    public void delete_newly_created_booking() {
        // given an existing booking
        BookingService service = new BookingService();
        int bookingID = service.createBooking(Booking.newInstance()).bookingid;
        // and an auth token
        String authToken = new BookingService().createAuthToken(
                "admin", "password123");
        // when deleting
        service.delete(bookingID, authToken);

        // then the booking no longer exists
        assertThat(service.doesBookingExist(bookingID)).isFalse();
        assertThat(service.listBookings())
                .doesNotContain(BookingID.of(bookingID));
    }

}
