package ar.edu.itba.paw.webapp.controller.util;

import java.net.URI;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

public class PaginationBuilder<T> {
    public static Response buildResponse(
        Response.ResponseBuilder responseBuilder,
        int page,
        int pageSize,
        int totalItems,
        Map<String, String> queryParams,
        UriBuilder baseUri
    ) {
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            baseUri.queryParam(entry.getKey(), entry.getValue());
        }

        if (page > 1) {
            URI firstPageUri = baseUri.replaceQueryParam("page", 1).replaceQueryParam("pageSize", pageSize).build();
            URI prevPageUri = baseUri.replaceQueryParam("page", page - 1).replaceQueryParam("pageSize", pageSize).build();
            responseBuilder.link(firstPageUri, "first");
            responseBuilder.link(prevPageUri, "prev");
        }

        if (page < totalPages) {
            URI nextPageUri = baseUri.replaceQueryParam("page", page + 1).replaceQueryParam("pageSize", pageSize).build();
            URI lastPageUri = baseUri.replaceQueryParam("page", totalPages).replaceQueryParam("pageSize", pageSize).build();
            responseBuilder.link(nextPageUri, "next");
            responseBuilder.link(lastPageUri, "last");
        }

        responseBuilder.header("X-Current-Page", page);
        responseBuilder.header("X-Total-Pages", totalPages);

        return responseBuilder.build();
    }
}
