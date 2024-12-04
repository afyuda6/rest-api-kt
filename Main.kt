import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import handlers.UserHandler
import java.io.OutputStream
import java.net.InetSocketAddress

class NotFoundHandler : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        val jsonResponse = """
            {
                "status": "Not Found",
                "code": 404
            }
        """.trimIndent()

        exchange.responseHeaders.add("Content-Type", "application/json")
        exchange.sendResponseHeaders(404, jsonResponse.toByteArray().size.toLong())

        exchange.responseBody.use { os: OutputStream -> os.write(jsonResponse.toByteArray()) }
    }
}

fun main() {

    connectToDatabase()

    val server = HttpServer.create(InetSocketAddress(6009), 0)
    server.createContext("/") { exchange ->
        val path = exchange.requestURI.path
        val query = exchange.requestURI.query

        if (path == "/users" || path == "/users/" && (query == null || query.isEmpty())) {
            UserHandler().handle(exchange)
        } else if (path.matches(Regex("^/users/([a-zA-Z0-9_-]+)$"))) {
            UserHandler().handle(exchange)
        } else {
            NotFoundHandler().handle(exchange)
        }
    }
    server.start()
}
