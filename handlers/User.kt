package handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.sql.DriverManager

class UserHandler : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        when (exchange.requestMethod) {
            "GET" -> handleReadUsers(exchange)
            "POST" -> handleCreateUser(exchange)
            "PUT" -> handleUpdateUser(exchange)
            "DELETE" -> handleDeleteUser(exchange)
            else -> handleMethodNotAllowed(exchange)
        }
    }

    private fun handleReadUsers(exchange: HttpExchange) {
        val dbUrl = "jdbc:sqlite:rest_api_kt.db"
        val users = mutableListOf<String>()

        DriverManager.getConnection(dbUrl).use { connection ->
            val query = "SELECT id, name FROM users"
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(query)
            while (resultSet.next()) {
                val user = """{
                    "id": ${resultSet.getInt("id")},
                    "name": "${resultSet.getString("name")}"
                }"""
                users.add(user)
            }
        }

        val jsonResponse = """
            {
                "status": "OK",
                "code": 200,
                "data": [${users.joinToString(",")}]
            }
        """.trimIndent()

        exchange.responseHeaders.add("Content-Type", "application/json")
        exchange.sendResponseHeaders(200, jsonResponse.toByteArray().size.toLong())
        exchange.responseBody.use { os: OutputStream -> os.write(jsonResponse.toByteArray()) }
    }

    private fun handleCreateUser(exchange: HttpExchange) {
        val requestBody = InputStreamReader(exchange.requestBody, StandardCharsets.UTF_8).readText()
        if (requestBody.isBlank()) {
            val jsonResponse = """
            {
                "status": "Bad Request",
                "code": 400,
                "errors": "Missing 'name' parameter"
            }
            """.trimIndent()

            exchange.responseHeaders.add("Content-Type", "application/json")
            exchange.sendResponseHeaders(400, jsonResponse.toByteArray().size.toLong())
            exchange.responseBody.use { os: OutputStream -> os.write(jsonResponse.toByteArray()) }
            return
        }

        val params = requestBody.split("&").associate {
            val (key, value) = it.split("=")
            URLDecoder.decode(key, StandardCharsets.UTF_8.name()) to URLDecoder.decode(
                value,
                StandardCharsets.UTF_8.name()
            )
        }

        val name = params["name"]
        if (name != null) {
            val dbUrl = "jdbc:sqlite:rest_api_kt.db"
            DriverManager.getConnection(dbUrl).use { connection ->
                val query = "INSERT INTO users(name) VALUES (?)"
                val statement = connection.prepareStatement(query)
                statement.setString(1, name)
                statement.executeUpdate()
            }

            val jsonResponse = """
            {
                "status": "Created",
                "code": 201
            }
            """.trimIndent()

            exchange.responseHeaders.add("Content-Type", "application/json")
            exchange.sendResponseHeaders(201, jsonResponse.toByteArray().size.toLong())
            exchange.responseBody.use { os: OutputStream -> os.write(jsonResponse.toByteArray()) }
        } else {
            val jsonResponse = """
            {
                "status": "Bad Request",
                "code": 400,
                "errors": "Missing 'name' parameter"
            }
            """.trimIndent()

            exchange.responseHeaders.add("Content-Type", "application/json")
            exchange.sendResponseHeaders(400, jsonResponse.toByteArray().size.toLong())
            exchange.responseBody.use { os: OutputStream -> os.write(jsonResponse.toByteArray()) }
        }
    }

    private fun handleUpdateUser(exchange: HttpExchange) {
        val requestBody = InputStreamReader(exchange.requestBody, StandardCharsets.UTF_8).readText()
        if (requestBody.isBlank()) {
            val jsonResponse = """
            {
                "status": "Bad Request",
                "code": 400,
                "errors": "Missing 'id' or 'name' parameter"
            }
            """.trimIndent()

            exchange.responseHeaders.add("Content-Type", "application/json")
            exchange.sendResponseHeaders(400, jsonResponse.toByteArray().size.toLong())
            exchange.responseBody.use { os: OutputStream -> os.write(jsonResponse.toByteArray()) }
            return
        }

        val params = requestBody.split("&").associate {
            val (key, value) = it.split("=")
            URLDecoder.decode(key, StandardCharsets.UTF_8.name()) to URLDecoder.decode(
                value,
                StandardCharsets.UTF_8.name()
            )
        }

        val name = params["name"]
        val id = params["id"]
        if (name != null && id != null) {
            val dbUrl = "jdbc:sqlite:rest_api_kt.db"
            DriverManager.getConnection(dbUrl).use { connection ->
                val query = "UPDATE users SET name = ? WHERE id = ?"
                val statement = connection.prepareStatement(query)
                statement.setString(1, name)
                statement.setInt(2, id.toInt())
                statement.executeUpdate()
            }

            val jsonResponse = """
            {
                "status": "OK",
                "code": 200
            }
            """.trimIndent()

            exchange.responseHeaders.add("Content-Type", "application/json")
            exchange.sendResponseHeaders(200, jsonResponse.toByteArray().size.toLong())
            exchange.responseBody.use { os: OutputStream -> os.write(jsonResponse.toByteArray()) }
        } else {
            val jsonResponse = """
            {
                "status": "Bad Request",
                "code": 400,
                "errors": "Missing 'id' or 'name' parameter"
            }
            """.trimIndent()

            exchange.responseHeaders.add("Content-Type", "application/json")
            exchange.sendResponseHeaders(400, jsonResponse.toByteArray().size.toLong())
            exchange.responseBody.use { os: OutputStream -> os.write(jsonResponse.toByteArray()) }
        }
    }

    private fun handleDeleteUser(exchange: HttpExchange) {
        val requestBody = InputStreamReader(exchange.requestBody, StandardCharsets.UTF_8).readText()
        if (requestBody.isBlank()) {
            val jsonResponse = """
            {
                "status": "Bad Request",
                "code": 400,
                "errors": "Missing 'id' parameter"
            }
            """.trimIndent()

            exchange.responseHeaders.add("Content-Type", "application/json")
            exchange.sendResponseHeaders(400, jsonResponse.toByteArray().size.toLong())
            exchange.responseBody.use { os: OutputStream -> os.write(jsonResponse.toByteArray()) }
            return
        }

        val params = requestBody.split("&").associate {
            val (key, value) = it.split("=")
            URLDecoder.decode(key, StandardCharsets.UTF_8.name()) to URLDecoder.decode(
                value,
                StandardCharsets.UTF_8.name()
            )
        }

        val id = params["id"]
        if (id != null) {
            val dbUrl = "jdbc:sqlite:rest_api_kt.db"
            DriverManager.getConnection(dbUrl).use { connection ->
                val query = "DELETE FROM users WHERE id = ?"
                val statement = connection.prepareStatement(query)
                statement.setInt(1, id.toInt())
                statement.executeUpdate()
            }

            val jsonResponse = """
            {
                "status": "OK",
                "code": 200
            }
            """.trimIndent()

            exchange.responseHeaders.add("Content-Type", "application/json")
            exchange.sendResponseHeaders(200, jsonResponse.toByteArray().size.toLong())
            exchange.responseBody.use { os: OutputStream -> os.write(jsonResponse.toByteArray()) }
        } else {
            val jsonResponse = """
            {
                "status": "Bad Request",
                "code": 400,
                "errors": "Missing 'id' parameter"
            }
            """.trimIndent()

            exchange.responseHeaders.add("Content-Type", "application/json")
            exchange.sendResponseHeaders(400, jsonResponse.toByteArray().size.toLong())
            exchange.responseBody.use { os: OutputStream -> os.write(jsonResponse.toByteArray()) }
        }
    }

    private fun handleMethodNotAllowed(exchange: HttpExchange) {
        val jsonResponse = """
            {
                "status": "Method Not Allowed",
                "code": 405
            }
        """.trimIndent()

        exchange.responseHeaders.add("Content-Type", "application/json")
        exchange.sendResponseHeaders(405, jsonResponse.toByteArray().size.toLong())

        exchange.responseBody.use { os: OutputStream -> os.write(jsonResponse.toByteArray()) }
    }
}
