package data;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import logica.*;

public class DatabaseConnection {
    // Configurações do Banco de Dados

    private static final String URL =  "jdbc:mysql://localhost:3306/sistema_horarios?serverTimezone=UTC"; // Substitua com o nome do seu banco
    private static final String USER = "me"; // Substitua pelo seu usuário do MySQL
    private static final String PASSWORD = "mypass"; // Substitua pela sua senha

    private static Connection connection = null;

    // Método para obter a conexão
    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Certifique-se de que o driver JDBC está no classpath
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Driver JDBC não encontrado. Certifique-se de que o MySQL Connector/J está no classpath.", e);
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao conectar ao banco de dados. Verifique as configurações.", e);
            }
        }
        return connection;
    }

    // Método para fechar a conexão
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }

}
