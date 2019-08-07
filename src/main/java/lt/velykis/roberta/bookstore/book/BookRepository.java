package lt.velykis.roberta.bookstore.book;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BookRepository {

    private static final File DATA_STORAGE_FILE = new File("bookstore-data/books.json");
    private Map<String, Book> books = !DATA_STORAGE_FILE.exists()
            ? init()
            : toBarcodeMap(readFromFile().stream());


    private static Map<String, Book> init() {

        return toBarcodeMap(Stream.of(
                new Book("Effective Java", "Joshua Bloch", "1212", 5L, new BigDecimal(10.00)),
                new Book("Thinking in Java", "Bruce Eckel", "2323", 10L, new BigDecimal(12.00)),
                new AntiqueBook("AntiqueBook1", "John Smith", "4545", 8L, new BigDecimal(10.00), 1880),
                new AntiqueBook("AntiqueBook2", "Joe Jones", "5656", 10L, new BigDecimal(12.00), 1789),
                new Journal("Journal1", "Tim Bloch", "6767", 9L, new BigDecimal(10.00), 5),
                new Journal("Journal2", "Andrew Cook", "7878", 11L, new BigDecimal(12.00), 7)
        ));
    }

    private static Map<String, Book> toBarcodeMap(Stream<Book> books) {
        return books.collect(Collectors.toMap(Book::getBarcode, b -> b));
    }

    public List<Book> getAll() {
        return new ArrayList<>(books.values());
    }

    public Optional<Book> find(String barcode) {
        return Optional.ofNullable(books.get(barcode));
    }

    public Book addNew(Book book) {
        books.put(book.getBarcode(), book);
        saveToFile();
        return book;
    }

    public Book update(Book book) {
        books.put(book.getBarcode(), book);
        saveToFile();
        return book;
    }

    public void delete(String barcode) {
        books.remove(barcode);
        saveToFile();
    }

    public void deleteAll() {
        books.clear();
        saveToFile();
    }

    private List<Book> readFromFile() {

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(DATA_STORAGE_FILE, new TypeReference<List<Book>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveToFile() {

        List<Book> sortedBooks = books.values().stream()
                .sorted(Comparator.comparing(Book::getName))
                .collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true);
        ObjectWriter writer = mapper.writerFor(new TypeReference<List<Book>>() {
        });
        try {
            DATA_STORAGE_FILE.getParentFile().mkdirs();
            writer.writeValue(DATA_STORAGE_FILE, sortedBooks);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
