package ar.edu.itba.paw.webapp.dto.output;

import java.net.URI;
import java.util.Collection;

public class TemplatedLinkDTO {
    
    private String href;
    private boolean templated;

    public TemplatedLinkDTO() {
    }

    public TemplatedLinkDTO(String href, boolean templated) {
        this.href = href;
        this.templated = templated;
    }

    /**
     * Creates a templated link with query parameters
     * @param baseUri The base URI
     * @param queryParams The query parameter names to template
     * @return A TemplatedLinkDTO with RFC 6570 query parameter syntax
     */
    public static TemplatedLinkDTO withQueryParams(URI baseUri, Collection<String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return new TemplatedLinkDTO(baseUri.toString(), false);
        }
        
        StringBuilder href = new StringBuilder(baseUri.toString());
        href.append("{?");
        boolean first = true;
        for (String param : queryParams) {
            if (!first) {
                href.append(",");
                first = false;
            }
            href.append(param);
        }
        href.append("}");
        
        return new TemplatedLinkDTO(href.toString(), true);
    }

    public static TemplatedLinkDTO of(URI uri) {
        if(uri == null) return null;
        return new TemplatedLinkDTO(uri.toString(), false);
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public boolean isTemplated() {
        return templated;
    }

    public void setTemplated(boolean templated) {
        this.templated = templated;
    }
}
