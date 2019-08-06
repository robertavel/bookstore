package lt.velykis.roberta.bookstore.book;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Journal extends Book {

    @Min(value = 1, message = "index must be between 1-10")
    @Max(value = 10, message = "index must be between 1-10")
    private int index;

    public Journal(String name, String author, String barcode, Long quantity, BigDecimal price, int index) {

        super(name, author, barcode, quantity, price);
        this.index = index;
    }

    @Override
    public BigDecimal calculateTotalPrice() {
        return (super.price.multiply(new BigDecimal(super.quantity)))
                .multiply(new BigDecimal(index));
    }
}
