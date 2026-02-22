package ar.edu.itba.paw.webapp.controller.util;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class URIHelper {

    public static Long getId(String uri, URI uriExpected) {
        if (uri == null || uriExpected == null) return null;

        String basePath = uriExpected.getPath();

        basePath = basePath.replaceAll("\\{[^/]+\\}$", "");

        if (!basePath.endsWith("/")) {

            basePath += "/";

        }
        Pattern pattern = Pattern.compile(Pattern.quote(basePath) + "(\\d+)$");
        return extractId(URI.create(uri), pattern);
    }
    
    public static List<Long> getIds(List<String> uris, URI uriExpected) {
        if (uris == null || uris.isEmpty() || uriExpected == null) return Collections.emptyList();

        String basePath = uriExpected.getPath();
        basePath = basePath.replaceAll("\\{[^/]+\\}$", "");
        if (!basePath.endsWith("/")) {
            basePath += "/";
        }
        
        Pattern pattern = Pattern.compile(Pattern.quote(basePath) + "(\\d+)$");

        return uris.stream()
                .map(URI::create)
                .map(uri -> extractId(uri, pattern))
                .collect(Collectors.toList());
    }

    private static Long extractId(URI uri, Pattern pathPattern) {
        Matcher matcher = pathPattern.matcher(uri.getPath());

        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                "URI does not match expected endpoint: " + uri
            );
        }

        return Long.valueOf(matcher.group(1));
    }

}
