package ar.edu.itba.paw.form;

@Deprecated
public class SearchForm {
    private String query;

    // Getter
    public String getQuery() {
        return query;
    }

    // Setter
    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "SearchForm={query=" + query + "}";
    }
}
