package data;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import logica.*;

public class UCDAO {
    private Connection connection;

    public UCDAO() {
        this.connection = DatabaseConnection.getConnection();
        criarTabela();
    }

    private void criarTabela() {
        String sql = "CREATE TABLE IF NOT EXISTS ucs ("
                + "codigoUC VARCHAR(50) PRIMARY KEY, "
                + "nomeUC VARCHAR(100) NOT NULL"
                + ")";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabela de UCs", e);
        }
    }

    public void insert(UC uc) throws SQLException {
        String sql = "INSERT INTO ucs (codigoUC, nomeUC) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uc.getCodigoUC());
            stmt.setString(2, uc.getNomeUC());
            stmt.executeUpdate();
        }
    }

    public UC findByCodigo(String codigoUC) throws SQLException {
        String sql = "SELECT * FROM ucs WHERE codigoUC = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, codigoUC);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UC(
                            rs.getString("codigoUC"),
                            rs.getString("nomeUC")
                    );
                }
            }
        }

        return null;
    }

    // Adicionar o método findAll
    public List<UC> findAll() throws SQLException {
        List<UC> ucs = new ArrayList<>();
        String sql = "SELECT * FROM ucs";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                UC uc = new UC(
                        rs.getString("codigoUC"),
                        rs.getString("nomeUC")
                );
                ucs.add(uc);
            }
        }

        return ucs;
    }
}
