package logica;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import data.*;

public class CSVParser {
    public static List<Aluno> parseCSV(String filename, AlunoDAO alunoDAO, UCDAO ucDAO) throws IOException, SQLException {
        File file = new File(filename);

        // Verificar se o ficheiro existe e é válido
        if (!file.exists() || !filename.endsWith(".csv")) {
            System.out.println("[Ficheiro importado não é válido]");
            return new ArrayList<>();
        }

        List<Aluno> alunos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Pula o cabeçalho
                }

                String[] data = line.split(";");
                if (data.length >= 8) {
                    String nome = data[0].trim();
                    String numero = data[1].trim();
                    String email = data[2].trim();
                    String password = "passaluno"; // Palavra-passe padrão
                    char genero = data[5].trim().charAt(0);
                    String motivoRegimeEspecial = data[6].trim();
                    int ano = Integer.parseInt(data[7].trim());
                    String codigoUC = data[3].trim();
                    String nomeUC = data[4].trim();

                    // Certificar que a UC existe
                    UC uc = ucDAO.findByCodigo(codigoUC);
                    if (uc == null) {
                        uc = new UC(codigoUC, nomeUC);
                        ucDAO.insert(uc);
                    }

                    // Certificar que o aluno existe
                    Aluno aluno = alunoDAO.findById(numero);
                    if (aluno == null) {
                        if (!motivoRegimeEspecial.isEmpty() && !motivoRegimeEspecial.equalsIgnoreCase("NaN")) {
                            aluno = new AlunoComEstatutoEspecial(nome, numero, email, password, genero, ano, motivoRegimeEspecial);
                        } else {
                            aluno = new Aluno(nome, numero, email, password, genero, ano);
                        }
                        alunoDAO.insert(aluno); // Insere o aluno antes de associá-lo
                    }

                    // Associar a UC ao aluno
                    if (!aluno.getUcIds().contains(codigoUC)) {
                        aluno.adicionarUc(codigoUC);
                        alunoDAO.update(aluno); // Atualiza no banco
                    }

                    alunos.add(aluno);
                }
            }
        }

        return alunos;
    }

}
