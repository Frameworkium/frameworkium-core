package com.frameworkium.integration.restfulbooker.api.tests;

import com.frameworkium.lite.api.tests.BaseAPITest;
import com.frameworkium.integration.restfulbooker.api.dto.booking.Booking;
import com.frameworkium.integration.restfulbooker.api.dto.booking.BookingID;
import com.frameworkium.integration.restfulbooker.api.dto.booking.search.SearchParamsMapper;
import com.frameworkium.integration.restfulbooker.api.service.booking.BookingService;
import com.frameworkium.integration.restfulbooker.api.service.ping.PingService;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// app resets every 10m, so could happen in the middle of this test
@Test(enabled = false) // currently down
public class SearchBookerTest extends BaseAPITest {

    @BeforeClass
    public void ensure_site_is_up_by_using_ping_service() {
        assertThat(new PingService().ping()).isEqualTo("Created");
    }

    public void search_for_existing_records_by_name() {
        BookingService service = new BookingService();
        BookingID existingID = service.listBookings().get(1);
        Booking booking = service.getBooking(existingID.bookingid);

        List<BookingID> bookingIDs = service.search(
                SearchParamsMapper.namesOfBooking(booking));

        assertThat(bookingIDs).contains(existingID);
    }

    public void search_for_existing_records_by_date() {
        BookingService service = new BookingService();
        BookingID existingID = service.listBookings().get(3);
        Booking booking = service.getBooking(existingID.bookingid);

        List<BookingID> bookingIDs = service.search(
                SearchParamsMapper.datesOfBooking(booking));

        // TODO: move to dedicated test
        throw new SkipException("Known bug in service, dates not inclusive");
        // assertThat(bookingIDs).contains(existingID);
    }

}
