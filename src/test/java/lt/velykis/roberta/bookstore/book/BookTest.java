package lt.velykis.roberta.bookstore.book;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class BookTest {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new Book("book1", "author1", "barcode1", 12L, new BigDecimal(12)), "144"},
                {new Book("book2", "author2", "barcode2", 122L, new BigDecimal(10)), "1220"},
                {new AntiqueBook("antiqueBook1", "author3", "barcode3", 2L, new BigDecimal(10), 1819), "400"},
                {new AntiqueBook("antiqueBook2", "author4", "barcode5", 5L, new BigDecimal(10), 1719), "1500"},
                {new Journal("journal1", "author6", "barcode7", 3L, new BigDecimal(2), 4), "24"},
                {new Journal("journal2", "author8", "barcode9", 3L, new BigDecimal(3), 2), "18"}
        });
    }

    @Parameter
    public Book book;

    @Parameter(1)
    public String expectedTotal;

    @Test
    public void calculateTotalPriceTest() {
        Assertions.assertThat(book.calculateTotalPrice()).isEqualTo(expectedTotal);
    }

}
