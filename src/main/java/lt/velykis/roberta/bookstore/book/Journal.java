package lt.velykis.roberta.bookstore.book;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Journal extends Book {
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
