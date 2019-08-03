package lt.velykis.roberta.bookstore.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    protected String name;
    protected String author;
    protected String barcode;
    protected Long quantity;
    protected BigDecimal price;

    public BigDecimal calculateTotalPrice() {
        return price.multiply(new BigDecimal(quantity));
    }

}
