import java.sql.Connection
import java.sql.DriverManager

fun connectToDatabase(): Connection {
    val url = "jdbc:sqlite:rest_api_kt.db"
    return DriverManager.getConnection(url).apply {
        createStatement().execute("DROP TABLE IF EXISTS users;")
        createStatement().execute(
            """
                CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL
                );
                """
        )
    }
}
