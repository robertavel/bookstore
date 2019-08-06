package lt.velykis.roberta.bookstore.book;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.List;


@Path("books")
public class BookResource {

    private final BookRepository repo;

    @Inject
    public BookResource(BookRepository repo) {
        this.repo = repo;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> findAll() {
        return repo.getAll();
    }

    @GET
    @Path("/{barcode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Book find(@PathParam("barcode") String barcode) {

        return repo.find(barcode)
                .orElseThrow(() -> new NotFoundException("Book not found"));
    }

    @GET
    @Path("/{barcode}/total")
    @Produces(MediaType.APPLICATION_JSON)
    public BigDecimal getTotalPrice(@PathParam("barcode") String barcode) {

        return repo.find(barcode).map(Book::calculateTotalPrice)
                .orElseThrow(() -> new NotFoundException("Book not found"));
    }

    @POST
    public void add(@Valid Book book) {

        if (repo.find(book.getBarcode()).isPresent()) {
            throw new BadRequestException();
        }
        repo.addNew(book);
    }

    @PUT
    @Path("/{barcode}")
    public void update(@PathParam("barcode") String barcode, @Valid Book book) {

        if (!repo.find(barcode).isPresent()) {
            throw new NotFoundException();
        }
        repo.update(barcode, book);
    }

    @DELETE
    @Path("/{barcode}")
    public void delete(@PathParam("barcode") String barcode) {

        if (!repo.find(barcode).isPresent()) {
            throw new NotFoundException();
        }
        repo.delete(barcode);
    }

    @DELETE
    public void deleteAll() {
        repo.deleteAll();
    }
}
