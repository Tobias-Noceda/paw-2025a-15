package ar.edu.itba.paw.webapp.controller.util;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

public class CacheHelper {

    public static final int MAX_AGE = 31536000;

    public static Response.ResponseBuilder evaluate(Request request, EntityTag eTag) {
        return request.evaluatePreconditions(eTag);
    }

    public static void addCacheHeaders(Response.ResponseBuilder builder, EntityTag eTag) {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE);
        cacheControl.setPrivate(true);

        builder.tag(eTag);
        builder.cacheControl(cacheControl);
    }
}