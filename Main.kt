import com.sun.net.httpserver.HttpServer
import handlers.UserHandler
import java.net.InetSocketAddress

fun main() {
    connectToDatabase()
    val server = HttpServer.create(InetSocketAddress(6009), 0)
    server.createContext("/") { exchange ->
        UserHandler().handle(exchange);
    }
    server.start()
}
