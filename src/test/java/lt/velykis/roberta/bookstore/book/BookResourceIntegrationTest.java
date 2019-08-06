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

    private static final String BOOK1_JSON =
            "{\"type\":\"book\",\"name\":\"book1\",\"author\":\"author1\",\"barcode\":\"barcode1\",\"quantity\":12,\"price\":12}";

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
    public void addBook_ok() {

        Mockito.when(repo.find("barcode1")).thenReturn(Optional.empty());
        Mockito.doNothing().when(repo).addNew(BOOK1);

        Response response = target("books").request()
                .post(Entity.json(BOOK1_JSON));

        assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());
        Mockito.verify(repo).addNew(BOOK1);

    }

    @Test
    public void addBook_badRequest() {

        Mockito.when(repo.find("barcode1")).thenReturn(Optional.of(BOOK1));
        Mockito.doNothing().when(repo).addNew(BOOK1);

        Response response = target("books").request()
                .post(Entity.json(BOOK1_JSON));

        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void editBook_ok() {
        Mockito.when(repo.find("barcode1")).thenReturn(Optional.of(BOOK1));
        Mockito.doNothing().when(repo).update("barcode1", BOOK1);

        Response response = target("books/barcode1").request()
                .put(Entity.json(BOOK1_JSON));

        assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());
        Mockito.verify(repo).update("barcode1", BOOK1);

    }

    @Test
    public void editBook_notFound() {
        Mockito.when(repo.find("barcode2")).thenReturn(Optional.empty());
        Mockito.doNothing().when(repo).update("barcode1", BOOK1);

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


}
