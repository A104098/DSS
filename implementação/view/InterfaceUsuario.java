// Atualização da InterfaceUsuario.java
package view;
import java.util.*;

import logica.*;
import data.*;
import java.io.IOException;
import java.sql.*;

public class InterfaceUsuario {
    private Scanner scanner = new Scanner(System.in);
    private AlunosGestor alunosGestor;
    private AulasGST aulasGST;
    private Map<String, Utilizador> usuarios; // Armazena usuários pelo número

    public InterfaceUsuario() {
        alunosGestor = new AlunosGestor();
        aulasGST = new AulasGST();
        usuarios = new HashMap<>(); // Inicializa o mapa de usuários
    }

    public void iniciar() {
        int opcao;

        do {
            System.out.println("\n--- Sistema de Gestão de Horários ---");
            System.out.println("1. Login");
            System.out.println("2. Registo");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir a linha

            switch (opcao) {
                case 1:
                    login();
                    break;
                case 2:
                    registro();
                    break;
                case 0:
                    System.out.println("Saindo do sistema...");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private void login() {
        int tentativas = 0;
        while (tentativas < 5) {
            System.out.print("Digite o número: ");
            String numero = scanner.nextLine();
            System.out.print("Digite a palavra-passe: ");
            String senha = scanner.nextLine();

            try {
                UtilizadorDAO utilizadorDAO = new UtilizadorDAO();
                String tipo = utilizadorDAO.validarEObterTipo(numero, senha);

                if (tipo == null) {
                    tentativas++;
                    System.out.println("Credenciais inválidas. Tentativa " + tentativas + " de 5.");
                    if (tentativas == 5) {
                        System.out.println("Número máximo de tentativas atingido. Voltando ao menu inicial.");
                        return;
                    }
                    continue;
                }

                System.out.println("Login bem-sucedido.");

                if (tipo.equals("D")) {
                    DiretorDeCurso diretor = new DiretorDeCurso("", numero, "", senha);
                    menuDiretor(diretor);
                } else if (tipo.equals("A")) {
                    Aluno aluno = new Aluno("", numero, "", senha, ' ', 1);
                    menuAluno(aluno);
                }
                return;
            } catch (SQLException e) {
                System.err.println("Erro ao realizar login: " + e.getMessage());
                return;
            }
        }
    }


    // Atualização da função registro
    private void registro() {
        System.out.print("Digite o número: ");
        String numero = scanner.nextLine();

        try {
            UtilizadorDAO utilizadorDAO = new UtilizadorDAO();

            if (utilizadorDAO.existe(numero)) {
                System.out.println("Usuário com esse número já está registado.");
                return;
            }

            System.out.print("Digite a palavra-passe: ");
            String senha = scanner.nextLine();
            System.out.print("Digite o email: ");
            String email = scanner.nextLine();

            System.out.println("É um aluno ou um diretor de curso? (A/D)");
            String tipo = scanner.nextLine().toUpperCase();

            if (!tipo.equals("A") && !tipo.equals("D")) {
                System.out.println("Opção inválida. Registro cancelado.");
                return;
            }

            if (tipo.equals("A")) {
                Aluno novoAluno = new Aluno("", numero, email, senha, ' ', 1);
                usuarios.put(numero, novoAluno);
                utilizadorDAO.insert(numero, senha, "A");
                System.out.println("Aluno registrado com sucesso e salvo na base de dados.");
            } else if (tipo.equals("D")) {
                System.out.print("Digite a palavra-passe de autorização do diretor: ");
                String authPass = scanner.nextLine();

                if (authPass.equals("1234")) {
                    DiretorDeCurso novoDiretor = new DiretorDeCurso("", numero, email, senha);
                    usuarios.put(numero, novoDiretor);
                    utilizadorDAO.insert(numero, senha, "D");
                    System.out.println("Diretor de curso registrado com sucesso e salvo na base de dados.");
                } else {
                    System.out.println("Palavra-passe de autorização inválida. Registro cancelado.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao registrar usuário na base de dados: " + e.getMessage());
        }
    }


    private void listarAlunosComHorarioVazio() {
        AlunoDAO alunoDAO = new AlunoDAO();
        try {
            // Buscar todos os alunos que não possuem aulas associadas nos horários
            List<Aluno> alunosSemHorario = alunoDAO.findAll(); // Buscar todos os alunos
            HorarioDAO horarioDAO = new HorarioDAO();

            List<Aluno> alunosSemAulas = new ArrayList<>();
            for (Aluno aluno : alunosSemHorario) {
                // Verificar se o horário do aluno não possui aulas
                if (!horarioDAO.alunoPossuiHorarioEAulas(aluno.getNumero())) {
                    alunosSemAulas.add(aluno);
                }
            }

            if (alunosSemAulas.isEmpty()) {
                System.out.println("Nenhum aluno com horário vazio.");
            } else {
                System.out.println("--- Alunos com Horário Vazio ---");
                for (Aluno aluno : alunosSemAulas) {
                    System.out.println("Número: " + aluno.getNumero() + ", Nome: " + aluno.getNome());
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar alunos com horário vazio: " + e.getMessage());
        }
    }


    private void menuDiretor(DiretorDeCurso diretor) {
        int opcao;
        do {
            System.out.println("\n--- Menu Diretor de Curso ---");
            System.out.println("1. Importar alunos de ficheiro");
            System.out.println("2. Listar alunos");
            System.out.println("3. Importar aulas de ficheiro");
            System.out.println("4. Apagar todas as tabelas do banco de dados");
            System.out.println("5. Criar horário manualmente");
            System.out.println("0. Logout");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir a linha

            switch (opcao) {
                case 1:
                    importarAlunos();
                    break;
                case 2:
                    listarAlunos();
                    break;
                case 3:
                    importarAulas();
                    break;
                case 4:
                    DatabaseUtils.apagarTabelas();
                    break;
                case 5:
                    criarHorarioManual();
                    break;
                case 0:
                    System.out.println("Logout realizado com sucesso.");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private void criarHorarioManual() {
        System.out.println("--- Lista de Alunos com Horário Vazio ---");
        listarAlunosComHorarioVazio();
        System.out.print("Digite o número do aluno para criar o horário: ");
        String numeroAluno = scanner.nextLine();

        try {
            HorarioDAO horarioDAO = new HorarioDAO();
            AulaDAO aulaDAO = new AulaDAO();

            // Validar se o aluno existe
            Aluno aluno = new AlunoDAO().findById(numeroAluno);
            if (aluno == null) {
                System.out.println("Aluno não encontrado.");
                return;
            }

            // Obter o horário existente ou criar um novo
            int idHorario = horarioDAO.buscarIdHorarioPorAluno(numeroAluno);
            if (idHorario == -1) {
                idHorario = horarioDAO.criarHorario(numeroAluno);
            }

            // Obter aulas já alocadas no horário do aluno
            List<Aula> aulasJaAlocadas = horarioDAO.buscarAulasPorHorario(idHorario);

            // Obter todas as aulas disponíveis
            List<Aula> aulasDisponiveis = aulaDAO.findAll();

            // Processo iterativo para alocar aulas
            while (!aulasDisponiveis.isEmpty()) {
                // Filtrar aulas disponíveis que não entram em conflito
                List<Aula> aulasFiltradas = new ArrayList<>();
                for (Aula aulaDisponivel : aulasDisponiveis) {
                    boolean conflito = false;
                    for (Aula aulaAlocada : aulasJaAlocadas) {
                        if (aulaAlocada.getCodigoUC().equals(aulaDisponivel.getCodigoUC()) ||
                                (aulaAlocada.getHorarioInicial().isBefore(aulaDisponivel.getHorarioFinal()) &&
                                        aulaAlocada.getHorarioFinal().isAfter(aulaDisponivel.getHorarioInicial()))) {
                            conflito = true;
                            break;
                        }
                    }
                    if (!conflito) {
                        aulasFiltradas.add(aulaDisponivel);
                    }
                }

                if (aulasFiltradas.isEmpty()) {
                    System.out.println("Não há mais aulas disponíveis sem conflito.");
                    break;
                }

                // Exibir aulas filtradas
                System.out.println("--- Aulas Disponíveis ---");
                for (int i = 0; i < aulasFiltradas.size(); i++) {
                    System.out.println((i + 1) + ". " + aulasFiltradas.get(i));
                }

                System.out.print("Escolha o número da aula para alocar (0 para terminar): ");
                int escolha = scanner.nextInt();
                scanner.nextLine(); // Consumir a linha

                if (escolha == 0) break;

                if (escolha > 0 && escolha <= aulasFiltradas.size()) {
                    Aula aulaSelecionada = aulasFiltradas.get(escolha - 1);
                    horarioDAO.adicionarAulasAoHorario(idHorario, List.of(aulaSelecionada.getId()));
                    System.out.println("Aula alocada com sucesso.");

                    // Atualizar as listas
                    aulasDisponiveis.remove(aulaSelecionada);
                    aulasJaAlocadas.add(aulaSelecionada);
                } else {
                    System.out.println("Opção inválida. Tente novamente.");
                }
            }

            System.out.println("Processo de alocação de aulas concluído.");

        } catch (SQLException e) {
            System.err.println("Erro ao criar o horário ou alocar aulas: " + e.getMessage());
        }
    }



    private List<Aula> filtrarAulasSemConflito(List<Aula> aulasDisponiveis, List<Aula> aulasNoHorario) {
        List<Aula> aulasSemConflito = new ArrayList<>();
        for (Aula aula : aulasDisponiveis) {
            boolean conflito = false;
            for (Aula aulaExistente : aulasNoHorario) {
                if (aula.getHorarioInicial().isBefore(aulaExistente.getHorarioFinal()) &&
                        aula.getHorarioFinal().isAfter(aulaExistente.getHorarioInicial())) {
                    conflito = true;
                    break;
                }
            }
            if (!conflito) {
                aulasSemConflito.add(aula);
            }
        }
        return aulasSemConflito;
    }






    private void menuAluno(Aluno aluno) {
        int opcao;
        do {
            System.out.println("\n--- Menu Aluno ---");
            System.out.println("1. Visualizar horário");
            System.out.println("0. Logout");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir a linha

            switch (opcao) {
                case 1:
                    visualizarHorario(aluno);
                    break;
                case 0:
                    System.out.println("Logout realizado com sucesso.");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private void visualizarHorario(Aluno aluno) {
        try {
            HorarioDAO horarioDAO = new HorarioDAO();
            // Garantir que todas as aulas são buscadas corretamente
            List<Aula> aulas = horarioDAO.buscarAulasPorHorario(horarioDAO.buscarIdHorarioPorAluno(aluno.getNumero()));

            if (aulas.isEmpty()) {
                System.out.println("Não há aulas associadas ao horário deste aluno.");
            } else {
                System.out.println("--- Horário do Aluno ---");
                for (Aula aula : aulas) {
                    System.out.printf(
                            "Aula ID: %d | Dia: %s | Hora: %s - %s | Turno: %s | Sala: %s | UC: %s%n",
                            aula.getId(),
                            aula.getDiaSemana(),
                            aula.getHorarioInicial(),
                            aula.getHorarioFinal(),
                            aula.getTurno().getTipo(),
                            aula.getTurno().getSala(),
                            aula.getCodigoUC()
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar o horário do aluno: " + e.getMessage());
        }
    }




    private void importarAlunos() {
        System.out.print("Digite o caminho do ficheiro CSV: ");
        String caminho = scanner.nextLine();

        AlunoDAO alunoDAO = new AlunoDAO();
        UCDAO ucDAO = new UCDAO();
        UtilizadorDAO users_dao = new UtilizadorDAO();
        // Passar a lista de utilizadores para o método de carregamento
        alunosGestor.carregarAlunosDeCSV(caminho, alunoDAO, ucDAO, usuarios,users_dao );

        System.out.println("Importação de alunos concluída com sucesso.");
    }


    private void importarAulas() {
        System.out.print("Digite o caminho do ficheiro CSV: ");
        String caminho = scanner.nextLine();

        AulaDAO aulaDAO = new AulaDAO();
        TurnoDAO turnoDAO = new TurnoDAO();

        try {
            AulasGST novasAulas = CSVParserAulas.parseCSV(caminho);

            for (Aula aula : novasAulas.getAulas()) {
                try {
                    // Inserir o turno no banco de dados, caso ainda não esteja presente
                    turnoDAO.insert(aula.getTurno());

                    // Inserir a aula no banco de dados
                    aulaDAO.insert(aula);

                    // Adicionar a aula ao gestor de aulas
                    aulasGST.adicionarAula(aula);
                } catch (java.sql.SQLException ex) { // Importa java.sql.SQLException
                    System.out.println("Erro ao salvar aula na base de dados: " + ex.getMessage());
                }
            }
            System.out.println("Aulas importadas com sucesso e salvas no banco de dados.");
        } catch (IOException ex) {
            System.out.println("Erro ao importar aulas: " + ex.getMessage());
        }
    }

    private void listarAlunos() {
        AlunoDAO alunoDAO = new AlunoDAO();
        try {
            System.out.println("--- Lista de Alunos no Banco de Dados ---");
            for (Aluno aluno : alunoDAO.findAll()) {
                System.out.println(aluno);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar alunos: " + e.getMessage());
        }
    }

}
