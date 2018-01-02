package com.frameworkium.integration.heroku.restfulbooker.api.tests;

import com.frameworkium.core.api.tests.BaseAPITest;
import com.frameworkium.integration.heroku.restfulbooker.api.dto.booking.Booking;
import com.frameworkium.integration.heroku.restfulbooker.api.dto.booking.BookingID;
import com.frameworkium.integration.heroku.restfulbooker.api.dto.booking.search.SearchParamsMapper;
import com.frameworkium.integration.heroku.restfulbooker.api.service.booking.BookingService;
import com.frameworkium.integration.heroku.restfulbooker.api.service.ping.PingService;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class SearchBookerTest extends BaseAPITest {

    @BeforeClass
    public void ensure_site_is_up_by_using_ping_service() {
        assertThat(new PingService().ping())
                .isEqualTo("Created");
    }

    @Test
    public void search_for_existing_records_by_name() {
        BookingService service = new BookingService();
        BookingID existingID = service.listBookings().get(1);
        Booking booking = service.getBooking(existingID.bookingid);

        List<BookingID> bookingIDs = service.search(
                SearchParamsMapper.namesOfBooking(booking));

        assertThat(bookingIDs).contains(existingID);
    }

    @Test
    public void search_for_existing_records_by_date() {
        BookingService service = new BookingService();
        BookingID existingID = service.listBookings().get(3);
        Booking booking = service.getBooking(existingID.bookingid);

        List<BookingID> bookingIDs = service.search(
                SearchParamsMapper.datesOfBooking(booking));

        throw new SkipException("Known bug in service, dates not inclusive");
        // assertThat(bookingIDs).contains(existingID);
    }

}
