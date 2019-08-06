package lt.velykis.roberta.bookstore.book;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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

    @NotEmpty
    protected String name;
    @NotEmpty
    protected String author;
    @NotEmpty
    protected String barcode;
    @NotNull
    @Min(0)
    protected Long quantity;
    @NotNull
    @Min(0)
    protected BigDecimal price;

    public BigDecimal calculateTotalPrice() {
        return price.multiply(new BigDecimal(quantity));
    }

}
