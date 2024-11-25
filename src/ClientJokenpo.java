import java.io.*;
import java.net.*;

public class ClientJokenpo {
    private static final String SERVER_IP = "192.168.208.79";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Conectado ao servidor!");

            while (true) {
                String serverMessage = in.readLine();

                if (serverMessage == null || serverMessage.contains("encerrado")) {
                    System.out.println("Conexão encerrada pelo servidor.");
                    break;
                }

                System.out.println(serverMessage);

                if (serverMessage.contains("Faça sua jogada")) {
                    String move;
                    while (true) {
                        System.out.print("Digite sua jogada (pedra, papel, tesoura ou sair): ");
                        move = console.readLine().trim().toLowerCase();
                        if (move.equals("pedra") || move.equals("papel") || move.equals("tesoura") || move.equals("sair")) {
                            break; // Entrada válida
                        }
                        System.out.println("Entrada inválida. Tente novamente.");
                    }
                    out.println(move);

                    if ("sair".equalsIgnoreCase(move)) {
                        System.out.println("Você saiu do jogo.");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
