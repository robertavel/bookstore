package lt.velykis.roberta.bookstore.book;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BookRepository {

    private Map<String, Book> books = init();

    private static Map<String, Book> init() {

        return Stream.of(
                new Book("Effective Java", "Joshua Bloch", "1212", 5L, new BigDecimal(10.00)),
                new Book("Thinking in Java", "Bruce Eckel", "2323", 10L, new BigDecimal(12.00))
        ).collect(Collectors.toMap(Book::getBarcode, b -> b));
    }

    public List<Book> getAll() {
        return new ArrayList<>(books.values());
    }

    public Optional<Book> find(String barcode) {
        return Optional.ofNullable(books.get(barcode));
    }

    public void addNew(Book book) {
        books.put(book.getBarcode(), book);
    }

    public void update(String barcode, Book book) {
        books.put(book.getBarcode(), book);
    }

    public void delete(String barcode) {
        books.remove(barcode);
    }

    public void deleteAll() {
        books.clear();
    }
}
