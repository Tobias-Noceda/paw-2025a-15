package ar.edu.itba.paw.webapp.controller.util;

import java.net.URI;
import java.time.LocalDate;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class DatePaginationBuilder<T> {
    public static Response buildResponse(
        Response.ResponseBuilder responseBuilder,
        LocalDate date,
        LocalDate minDate,
        LocalDate maxDate,
        Map<String, String> queryParams,
        UriInfo uriInfo
    ) {
        UriBuilder baseUri = uriInfo.getRequestUriBuilder();

        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            baseUri.queryParam(entry.getKey(), entry.getValue());
        }

        if (date.isAfter(minDate)) {
            URI firstPageUri = baseUri.replaceQueryParam("date", minDate).build();
            URI prevPageUri = baseUri.replaceQueryParam("date", date.minusDays(1)).build();
            responseBuilder.link(firstPageUri, "first");
            responseBuilder.link(prevPageUri, "prev");
        }

        if (date.isBefore(maxDate)) {
            URI nextPageUri = baseUri.replaceQueryParam("date", date.plusDays(1)).build();
            URI lastPageUri = baseUri.replaceQueryParam("date", maxDate).build();
            responseBuilder.link(nextPageUri, "next");
            responseBuilder.link(lastPageUri, "last");
        }

        responseBuilder.header("X-Current-Date", date);
        responseBuilder.header("X-Max-Date", maxDate);

        return responseBuilder.build();
    }
}
