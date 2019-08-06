package lt.velykis.roberta.bookstore;

import lt.velykis.roberta.bookstore.book.BookRepository;
import lt.velykis.roberta.bookstore.book.BookResource;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api")
public class Application extends ResourceConfig {

    public Application() {
        register(BookResource.class);
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindAsContract(BookRepository.class).in(Singleton.class);
            }
        });

        // send BAD REQUEST and error message when validation fails
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        packages("lt.velykis.roberta.bookstore");
    }
}

