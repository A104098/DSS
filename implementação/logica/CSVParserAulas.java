package logica;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class CSVParserAulas {
    public static AulasGST parseCSV(String filename) throws IOException {
        AulasGST aulasGST = new AulasGST();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Pula o cabeçalho
                }

                String[] data = line.split(";");
                if (data.length == 7) {
                    String codigoUC = data[0].trim();

                    // Normalizar horário para garantir o formato HH:mm
                    String horarioInicialRaw = data[1].trim();
                    String horarioFinalRaw = data[2].trim();
                    String dia = data[6].trim();
                    String horarioInicial = normalizarHorario(horarioInicialRaw);
                    String horarioFinal = normalizarHorario(horarioFinalRaw);


                    LocalTime inicio = LocalTime.parse(horarioInicial, DateTimeFormatter.ofPattern("HH:mm"));
                    LocalTime fim = LocalTime.parse(horarioFinal, DateTimeFormatter.ofPattern("HH:mm"));

                    String tipoTurno = data[3].trim();
                    int capacidade = Integer.parseInt(data[4].trim());
                    String sala = data[5].trim();

                    Turno turno = new Turno(tipoTurno, capacidade, sala);
                    Aula aula = new Aula(codigoUC, inicio, fim, dia,turno);
                    aulasGST.adicionarAula(aula);
                }
            }
        }
        return aulasGST;
    }

    // Método auxiliar para normalizar horários
    private static String normalizarHorario(String horario) {
        // Se o horário começar com um número menor que 10 sem o zero inicial, adicione o zero
        if (horario.matches("^\\d:\\d{2}$")) {
            return "0" + horario;
        }
        // Retorna o horário como está se não precisar de ajustes
        return horario;
    }
}
