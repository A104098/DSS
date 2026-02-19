package data;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtils {

    public static void apagarTabelas() {
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            // Desativar as verificações de chave estrangeira
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");

            // Apagar as tabelas
            stmt.executeUpdate("DROP TABLE IF EXISTS aulas");
            stmt.executeUpdate("DROP TABLE IF EXISTS turnos");
            stmt.executeUpdate("DROP TABLE IF EXISTS aluno_ucs");
            stmt.executeUpdate("DROP TABLE IF EXISTS alunos");
            stmt.executeUpdate("DROP TABLE IF EXISTS ucs");
            stmt.executeUpdate("DROP TABLE IF EXISTS horarios");
            stmt.executeUpdate("DROP TABLE IF EXISTS horario_aulas");
            stmt.executeUpdate("DROP TABLE IF EXISTS utilizadores");


            // Reativar as verificações de chave estrangeira
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");

            System.out.println("Todas as tabelas foram apagadas com sucesso.");
        } catch (SQLException e) {
            System.out.println("Erro ao apagar tabelas: " + e.getMessage());
        }
    }
}
