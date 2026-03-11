import com.sun.net.httpserver.HttpServer
import handlers.UserHandler
import java.net.InetSocketAddress

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 6009
    connectToDatabase()
    val server = HttpServer.create(InetSocketAddress(port), 0)
    server.createContext("/") { exchange ->
        UserHandler().handle(exchange);
    }
    server.start()
}