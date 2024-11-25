import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ServerJokenpo {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        System.out.println("Servidor iniciado na porta " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Aguardando jogadores...");

            Socket player1 = serverSocket.accept();
            System.out.println("Jogador 1 conectado!");
            PrintWriter out1 = new PrintWriter(player1.getOutputStream(), true);
            BufferedReader in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));

            out1.println("Bem-vindo ao Jokenpo! Você é o Jogador 1. Aguardando o Jogador 2...");

            Socket player2 = serverSocket.accept();
            System.out.println("Jogador 2 conectado!");
            PrintWriter out2 = new PrintWriter(player2.getOutputStream(), true);
            BufferedReader in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));

            out1.println("Jogador 2 conectado! O jogo começará.");
            out2.println("Bem-vindo ao Jokenpo! Você é o Jogador 2. O jogo começará.");

            ExecutorService pool = Executors.newFixedThreadPool(2);

            while (true) {
                Future<String> move1 = pool.submit(() -> {
                    out1.println("Faça sua jogada (pedra, papel, tesoura ou 'sair' para encerrar):");
                    return in1.readLine();
                });

                Future<String> move2 = pool.submit(() -> {
                    out2.println("Faça sua jogada (pedra, papel, tesoura ou 'sair' para encerrar):");
                    return in2.readLine();
                });

                String player1Move = move1.get();
                String player2Move = move2.get();

                if ("sair".equalsIgnoreCase(player1Move) || "sair".equalsIgnoreCase(player2Move)) {
                    out1.println("Jogo encerrado. Até a próxima!");
                    out2.println("Jogo encerrado. Até a próxima!");
                    break;
                }

                String result = determineWinner("Jogador 1", player1Move, "Jogador 2", player2Move);
                out1.println(result);
                out2.println(result);
            }

            pool.shutdown();
            player1.close();
            player2.close();
            System.out.println("Servidor encerrado.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String determineWinner(String name1, String move1, String name2, String move2) {
        if (move1.equalsIgnoreCase(move2)) {
            return "Empate! Ambos jogaram " + move1;
        }

        boolean player1Wins = (move1.equalsIgnoreCase("pedra") && move2.equalsIgnoreCase("tesoura")) ||
                              (move1.equalsIgnoreCase("tesoura") && move2.equalsIgnoreCase("papel")) ||
                              (move1.equalsIgnoreCase("papel") && move2.equalsIgnoreCase("pedra"));

        if (player1Wins) {
            return name1 + " venceu com " + move1 + " contra " + move2 + " de " + name2;
        } else {
            return name2 + " venceu com " + move2 + " contra " + move1 + " de " + name1;
        }
    }
}
