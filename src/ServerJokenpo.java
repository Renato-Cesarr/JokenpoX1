import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ServerJokenpo {
    private static final int PORTA = 12345;

    public static void main(String[] args) {
        System.out.println("Servidor iniciado na porta " + PORTA);

        try (ServerSocket servidorSocket = new ServerSocket(PORTA)) {
            System.out.println("Aguardando jogadores...");

            Socket jogador1 = servidorSocket.accept();
            System.out.println("Jogador 1 conectado!");
            PrintWriter saidaJogador1 = new PrintWriter(jogador1.getOutputStream(), true);
            BufferedReader entradaJogador1 = new BufferedReader(new InputStreamReader(jogador1.getInputStream()));

            Socket jogador2 = servidorSocket.accept();
            System.out.println("Jogador 2 conectado!");
            PrintWriter saidaJogador2 = new PrintWriter(jogador2.getOutputStream(), true);
            BufferedReader entradaJogador2 = new BufferedReader(new InputStreamReader(jogador2.getInputStream()));

            saidaJogador1.println("Bem-vindo ao Jokenpo! Você é o Jogador 1. Aguardando o Jogador 2...");
            saidaJogador2.println("Bem-vindo ao Jokenpo! Você é o Jogador 2. O jogo começará.");
            saidaJogador1.println("Jogador 2 conectado! O jogo começará.");

            ExecutorService pool = Executors.newFixedThreadPool(2);

            while (true) {
                String jogadaJogador1 = obterJogada(pool, saidaJogador1, entradaJogador1);
                String jogadaJogador2 = obterJogada(pool, saidaJogador2, entradaJogador2);

                if ("sair".equalsIgnoreCase(jogadaJogador1) || "sair".equalsIgnoreCase(jogadaJogador2)) {
                    saidaJogador1.println("Jogo encerrado. Até a próxima!");
                    saidaJogador2.println("Jogo encerrado. Até a próxima!");
                    break;
                }

                String resultado = determinarVencedor("Jogador 1", jogadaJogador1, "Jogador 2", jogadaJogador2);
                saidaJogador1.println(resultado);
                saidaJogador2.println(resultado);
            }

            pool.shutdown();
            jogador1.close();
            jogador2.close();
            System.out.println("Servidor encerrado.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String obterJogada(ExecutorService pool, PrintWriter saida, BufferedReader entrada) throws Exception {
        Future<String> futuroJogada = pool.submit(() -> {
            saida.println("Faça sua jogada (pedra, papel, tesoura ou 'sair' para encerrar):");
            return entrada.readLine().trim().toLowerCase();
        });
        return futuroJogada.get();
    }

    private static String determinarVencedor(String nome1, String jogada1, String nome2, String jogada2) {
        if (jogada1.equals(jogada2)) {
            return "Empate! Ambos jogaram " + jogada1;
        }

        boolean jogador1Venceu = (jogada1.equals("pedra") && jogada2.equals("tesoura")) ||
                                  (jogada1.equals("tesoura") && jogada2.equals("papel")) ||
                                  (jogada1.equals("papel") && jogada2.equals("pedra"));

        if (jogador1Venceu) {
            return nome1 + " venceu com " + jogada1 + " contra " + jogada2 + " de " + nome2;
        } else {
            return nome2 + " venceu com " + jogada2 + " contra " + jogada1 + " de " + nome1;
        }
    }
}
