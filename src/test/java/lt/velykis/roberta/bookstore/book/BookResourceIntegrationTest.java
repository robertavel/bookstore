package lt.velykis.roberta.bookstore.book;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.mockito.Mockito;

import javax.inject.Singleton;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class BookResourceIntegrationTest extends JerseyTest {

    private final BookRepository repo = Mockito.mock(BookRepository.class);

    private final static Book BOOK1 = new Book("book1", "author1", "barcode1", 12L, new BigDecimal(12));
    private final static Book BOOK2 = new Book("book2", "author2", "barcode2", 122L, new BigDecimal(122));

    private final static Book JOURNAL_WITH_INVALID_INDEX = new Journal("journal", "author", "barcode", 12L, new BigDecimal(122), 15);
    private final static Book ABOOK_WITH_INVALID_YEAR = new AntiqueBook("aBook", "author", "barcode", 12L, new BigDecimal(12), 2000);


    private static final String BOOK1_JSON =
            "{\"type\":\"book\",\"name\":\"book1\",\"author\":\"author1\",\"barcode\":\"barcode1\",\"quantity\":12,\"price\":12}";
    private static final String BOOK2_JSON =
            "{\"type\":\"book\",\"name\":\"book2\",\"author\":\"author2\",\"barcode\":\"barcode2\",\"quantity\":1,\"price\":5}";
    private static final String JOURNAL_WITH_INVALID_INDEX_JSON =
            "{\"type\":\"journal\",\"name\":\"journal\",\"author\":\"author\",\"barcode\":\"barcode\",\"quantity\":12,\"price\":12,\"index\":15}";
    private static final String ABOOK_WITH_INVALID_YEAR_JSON =
            "{\"type\":\"antiqueBook\",\"name\":\"aBook\",\"author\":\"author\",\"barcode\":\"barcode\",\"quantity\":12,\"price\":12,\"year\":2000}";

    private final static List<Book> BOOKS = Arrays.asList(
            BOOK1,
            BOOK2,
            new AntiqueBook("antiqueBook1", "author3", "barcode3", 3L, new BigDecimal(12), 1890),
            new AntiqueBook("antiqueBook2", "author4", "barcode4", 3L, new BigDecimal(12), 1700),
            new Journal("journal1", "author5", "barcode5", 3L, new BigDecimal(12), 4),
            new Journal("journal2", "author6", "barcode6", 4L, new BigDecimal(15), 1)
    );

    @Override
    protected Application configure() {

        return new ResourceConfig()
                .register(BookResource.class)
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind(repo).to(BookRepository.class).in(Singleton.class);
                    }
                });
    }

    @Test
    public void getAllBooks_empty() {

        Mockito.when(repo.getAll()).thenReturn(Collections.emptyList());

        Response response = target("books").request().get();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(response.getHeaderString(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON);

        String responseContent = response.readEntity(String.class);
        assertThat(responseContent).isEqualTo("[]");
    }

    @Test
    public void getAllBooks() {

        Mockito.when(repo.getAll()).thenReturn(BOOKS);
        Response response = target("books").request().get();

        List<Book> responseContent2 = response.readEntity(new GenericType<List<Book>>() {
        });

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(responseContent2).isEqualTo(BOOKS);
    }

    @Test
    public void findBook() {

        Mockito.when(repo.find("barcode1")).thenReturn(Optional.ofNullable(BOOK1));
        Response response = target("books/barcode1").request().get();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        Book responseContent = response.readEntity(Book.class);
        assertThat(responseContent).isEqualTo(BOOK1);
    }

    @Test
    public void findBook_notFound_404() {

        Mockito.when(repo.find("unknownBarcode")).thenReturn(Optional.empty());
        Response response = target("books/unknownBarcode").request().get();

        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void calculateTotalPrice_ok() {

        Mockito.when(repo.find("barcode1")).thenReturn(Optional.of(BOOK1));
        Response response = target("books/barcode1/total").request().get();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(BOOK1.calculateTotalPrice()).isEqualTo(new BigDecimal(144));
    }

    @Test
    public void addBook_ok() {

        Mockito.when(repo.find("barcode1")).thenReturn(Optional.empty());
        Mockito.when(repo.addNew(BOOK1)).thenReturn(BOOK1);

        Response response = target("books").request()
                .post(Entity.json(BOOK1_JSON));

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        Mockito.verify(repo).addNew(BOOK1);

    }

    @Test
    public void addBook_badRequest() {

        Mockito.when(repo.find("barcode1")).thenReturn(Optional.of(BOOK1));

        Response response = target("books").request()
                .post(Entity.json(BOOK1_JSON));

        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void editBook_ok() {
        Mockito.when(repo.find("barcode1")).thenReturn(Optional.of(BOOK1));
        Mockito.when(repo.update(BOOK1)).thenReturn(BOOK1);

        Response response = target("books/barcode1").request()
                .put(Entity.json(BOOK1_JSON));

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void editBook_differentBarcode_badRequest() {
        Mockito.when(repo.find("barcode1")).thenReturn(Optional.of(BOOK1));

        Response response = target("books/barcode1").request()
                .put(Entity.json(BOOK2_JSON));

        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void editBook_notFound() {
        Mockito.when(repo.find("barcode2")).thenReturn(Optional.empty());

        Response response = target("books/barcode2").request()
                .put(Entity.json(BOOK1_JSON));

        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void deleteBook() {
        Mockito.when(repo.find("barcode1")).thenReturn(Optional.of(BOOK1));
        Mockito.doNothing().when(repo).delete("barcode1");

        Response response = target("books/barcode1").request().delete();

        assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());
        Mockito.verify(repo).delete("barcode1");
    }

    @Test
    public void deleteBook_notFound() {
        Mockito.when(repo.find("badBarcode")).thenReturn(Optional.empty());
        Mockito.doNothing().when(repo).delete("badBarcode");

        Response response = target("books/badBarcode").request().delete();

        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void deleteAll() {
        Mockito.doNothing().when(repo).deleteAll();

        Response response = target("books").request().delete();

        assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());
        Mockito.verify(repo).deleteAll();
    }

    @Test
    public void validation_AddJournalWithInvalidIndex_badRequest() {

        Mockito.when(repo.find("barcode")).thenReturn(Optional.empty());
        Mockito.when(repo.addNew(JOURNAL_WITH_INVALID_INDEX)).thenReturn(JOURNAL_WITH_INVALID_INDEX);

        Response response = target("books/barcode").request()
                .put(Entity.json(JOURNAL_WITH_INVALID_INDEX_JSON));

        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void validation_AddAntiqueBooklWithInvalidYear_badRequest() {

        Mockito.when(repo.find("barcode")).thenReturn(Optional.empty());
        Mockito.when(repo.addNew(ABOOK_WITH_INVALID_YEAR)).thenReturn(ABOOK_WITH_INVALID_YEAR);

        Response response = target("books/barcode").request()
                .put(Entity.json(ABOOK_WITH_INVALID_YEAR_JSON));

        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
    }
}
