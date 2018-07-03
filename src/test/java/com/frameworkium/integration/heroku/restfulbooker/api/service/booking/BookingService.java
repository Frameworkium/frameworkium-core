package com.frameworkium.integration.heroku.restfulbooker.api.service.booking;

import com.frameworkium.integration.heroku.restfulbooker.api.constant.BookerEndpoint;
import com.frameworkium.integration.heroku.restfulbooker.api.dto.booking.*;
import com.frameworkium.integration.heroku.restfulbooker.api.service.AbstractBookerService;
import com.google.common.collect.ImmutableMap;
import io.restassured.http.Method;

import java.util.List;
import java.util.Map;

public class BookingService extends AbstractBookerService {

    public List<BookingID> listBookings() {
        return get(BookerEndpoint.BOOKING.getUrl())
                .jsonPath().getList(".", BookingID.class);
    }

    public Booking getBooking(int id) {
        return get(BookerEndpoint.BOOKING_ID.getUrl(id))
                .as(Booking.class);
    }

    public CreateBookingResponse createBooking(Booking booking) {
        return post(booking, BookerEndpoint.BOOKING.getUrl())
                .as(CreateBookingResponse.class);
    }

    public List<BookingID> search(Map<String, String> searchParams) {
        return request(Method.GET, searchParams, BookerEndpoint.BOOKING.getUrl())
                .jsonPath().getList(".", BookingID.class);
    }

    public String createAuthToken(String username, String password) {
        return post(
                ImmutableMap.of("username", username, "password", password),
                BookerEndpoint.AUTH.getUrl())
                .jsonPath().get("token");
    }

    public void delete(int bookingID, String token) {
        getRequestSpec()
                .cookie("token", token)
                .delete(BookerEndpoint.BOOKING_ID.getUrl(bookingID))
                // API does not match documentation
                // .then()
                // .statusCode(HttpStatus.SC_NO_CONTENT)
                ;
    }

    public boolean doesBookingExist(int bookingID) {
        final int statusCode = getRequestSpec()
                .get(BookerEndpoint.BOOKING_ID.getUrl(bookingID))
                .then()
                .extract()
                .statusCode();
        if (statusCode == 200) {
            return true;
        } else if (statusCode == 404) {
            return false;
        } else {
            throw new IllegalStateException("Unexpected return code");
        }
    }
}
