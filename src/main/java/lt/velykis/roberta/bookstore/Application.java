package lt.velykis.roberta.bookstore;

import lt.velykis.roberta.bookstore.book.BookRepository;
import lt.velykis.roberta.bookstore.book.BookResource;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api")
public class Application extends ResourceConfig {

    public Application() {
        register(BookResource.class);
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindAsContract(BookRepository.class);
            }
        });

        packages("lt.velykis.roberta.bookstore");
    }
}

