package lt.velykis.roberta.bookstore.book;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AntiqueBook extends Book {

    @NotNull
    @Max(value = 1900, message = "must be before 1900")
    private int year;

    public AntiqueBook(String name, String author, String barcode, Long quantity, BigDecimal price, int year) {

        super(name, author, barcode, quantity, price);
        this.year = year;
    }

    @Override
    public BigDecimal calculateTotalPrice() {
        return (super.price.multiply(new BigDecimal(super.quantity)))
                .multiply(new BigDecimal((LocalDate.now().getYear() - year) / 10));
    }

}



