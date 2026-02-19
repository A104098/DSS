package data;

import java.sql.*;

public class UtilizadorDAO {
    private Connection connection;

    public UtilizadorDAO() {
        this.connection = DatabaseConnection.getConnection();
        criarTabela();
    }

    // Criação da tabela de utilizadores
    private void criarTabela() {
        String sql = "CREATE TABLE IF NOT EXISTS utilizadores ("
                + "id VARCHAR(50) PRIMARY KEY, "
                + "password VARCHAR(100) NOT NULL, "
                + "tipo CHAR(1) NOT NULL"
                + ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabela de utilizadores", e);
        }
    }

    // Inserir um novo utilizador
    public void insert(String id, String password, String tipo) throws SQLException {
        String sql = "INSERT INTO utilizadores (id, password, tipo) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, password);
            stmt.setString(3, tipo);
            stmt.executeUpdate();
        }
    }
    public boolean validar(String id, String password) throws SQLException {
        String sql = "SELECT COUNT(*) FROM utilizadores WHERE id = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public boolean existe(String id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM utilizadores WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // Atualizar a senha de um utilizador
    public void update(String id, String password) throws SQLException {
        String sql = "UPDATE utilizadores SET password = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, password);
            stmt.setString(2, id);
            stmt.executeUpdate();
        }
    }

    // Excluir um utilizador
    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM utilizadores WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }

    public String validarEObterTipo(String id, String password) throws SQLException {
        String sql = "SELECT tipo FROM utilizadores WHERE id = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tipo");
                }
            }
        }
        return null;
    }
}
