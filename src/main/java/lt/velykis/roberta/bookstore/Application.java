package lt.velykis.roberta.bookstore;

import lt.velykis.roberta.bookstore.book.BookRepository;
import lt.velykis.roberta.bookstore.book.BookResource;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;
import java.io.IOException;
import java.net.URI;

@ApplicationPath("/api")
public class Application extends ResourceConfig {

    private static final String BASE_URI = "http://localhost:8080/api";

    private static HttpServer startServer() {
        Application app = new Application();
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), app);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println(String.format("Bookstore application started at %s/books.\n"
                + "Hit enter to stop it...", BASE_URI));
        System.in.read();
        server.shutdownNow();
    }

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

