package lt.velykis.roberta.bookstore.book;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @Type(value = Book.class, name = "book"),
        @Type(value = AntiqueBook.class, name = "antiqueBook"),
        @Type(value = Journal.class, name = "journal")
})
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
