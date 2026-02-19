import view.*;
import data.*;
public class SistemaGestaoHorarios {

    public static void main(String[] args) {
        System.out.println("Iniciando conexão com o banco de dados...");
        DatabaseConnection.getConnection();
        InterfaceUsuario interfaceUsuario = new InterfaceUsuario();
        interfaceUsuario.iniciar();
    }
}
// 1 terminar as classes que faltam para colocar as coisas do parser
// 2 Na interface do usuario adicionar para o diretor de curso dar o ficheiro
// 3 Terminar parser de tudo para as respetivas classes
// 4 Meter as merdas na base de dados
// 5 Criar uma cena para o diretor de curso alocar manualmente as coisas
// a capacidade do turno T depende do tamanho da sala