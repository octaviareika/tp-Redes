package InterfaceSwing;
import java.io.*;
import java.net.*;
import java.util.Random;

public class Server {
    private static final int porta = 8081;
    private static final String[] palavras = {"cachorro", "gato", "elefante", "girafa", "leao", 
    "tigre", "pato", "ganso", "galinha", "papagaio", "pomba", "coruja", "urubu", "pelicano", "avestruz",
    "geladeira", "fogao", "bandeira", "gravata", "camisa", "sapato", "lua", "planta", "arvore", "flor"};

    private static final String[] palavrasDificeis = {"xenofobia", "inconstitucional", "paralelepipedo",
    "heterodoxo", "onomatopeia", "proparoxitona", "complexidade", "melancolia", "eclesiastico", "metamorfose",
    "tautologia" };
    private int maximasTentativas;

    public Server(int maximasTentativas) {
        this.maximasTentativas = maximasTentativas;
    }

    public Server() {
        this.maximasTentativas = 10; // valor padrão
    }

    // usa true ou false
    public boolean sortearDificuldade() {
        // implementação
        return true;
    }

    public String escolherPalavra(String[] palavra) {
        // implementação
        return palavra[0];
    }

    public int sortearTentativaFacil() {
        // implementação
        return 5;
    }

    public int sortearTentativaDificil() {
        // implementação
        return 3;
    }

    // getter
    public int getMaximoTentativas() {
        return maximasTentativas;
    }

    // criar uma mascara para a palavra
    public String criarMascara(String palavra) {
        StringBuilder mascara = new StringBuilder();
        for (int i = 0; i < palavra.length(); i++) {
            mascara.append("_");
        }
        return mascara.toString();
    }

    public void limparTela() {
        // implementação
    }

    public void inicializarJogo(BufferedReader entradaJogador1, PrintWriter saidaJogador1, 
                                BufferedReader entradaJogador2, PrintWriter saidaJogador2) throws IOException {
        try (entradaJogador1; saidaJogador1; entradaJogador2; saidaJogador2) {
            // implementação do jogo
            String mensagem;
            int jogadorAtual = 1;
            int tentativas = 0;
            String palavra = escolherPalavra(palavras);
            String mascara = criarMascara(palavra);

            while ((mensagem = (jogadorAtual == 1 ? entradaJogador1 : entradaJogador2).readLine()) != null) {
                PrintWriter saidaAtual = (jogadorAtual == 1 ? saidaJogador1 : saidaJogador2);
                BufferedReader entradaAtual = (jogadorAtual == 1 ? entradaJogador1 : entradaJogador2);
                PrintWriter saidaOutro = (jogadorAtual == 1 ? saidaJogador2 : saidaJogador1);

                // Situação atual
                saidaAtual.println("Sua vez. Palavra: " + mascara);

                System.out.println();
                saidaAtual.println("Vez de jogador " + jogadorAtual);
                saidaOutro.println("Aguarde a vez do jogador" + jogadorAtual);
                System.out.println();

                mensagem = entradaAtual.readLine();
                if (mensagem.startsWith("Tentativa: ")) {
                    String tentativa = mensagem.substring(11);
                    tentativas++;
                    if (palavra.contains(tentativa)) {
                        // Atualiza a máscara
                        for (int i = 0; i < palavra.length(); i++) {
                            if (palavra.charAt(i) == tentativa.charAt(0)) {
                                mascara = mascara.substring(0, i) + tentativa + mascara.substring(i + 1);
                            }
                        }
                        saidaAtual.println("Acertou! " + mascara);
                    } else {
                        saidaAtual.println("Errou! " + mascara);
                    }

                    if (mascara.equals(palavra)) {
                        saidaAtual.println("Parabéns! Você acertou a palavra: " + palavra);
                        saidaOutro.println("Fim de jogo!");
                        break;
                    }
                    if (tentativas >= this.getMaximoTentativas()) {
                        saidaAtual.println("Você perdeu! A palavra era: " + palavra);
                        saidaOutro.println("Parabéns! Você acertou a palavra: " + palavra);
                        break;
                    }

                    jogadorAtual = (jogadorAtual == 1) ? 2 : 1;
                    
                } else if (mensagem.startsWith("Chat: ")) {
                    String chat = mensagem.substring(6); // remove o termo "Chat: " da mensagem
                    saidaOutro.println("Chat do jogador: " + jogadorAtual + ": " + chat);
                }
            }
        }
    }

    public void iniciandoConexao() throws IOException {
        try (ServerSocket socket = new ServerSocket(porta)){
            System.out.println("Servidor iniciado na porta " + porta);
            System.out.println("Aguardando conexão de jogadores...");

            Socket jogador1 = socket.accept();
            System.out.println("Jogador 1 conectado!");

            Socket jogador2 = socket.accept();
            System.out.println("Jogador 2 conectado!");

            BufferedReader entradaJogador1 = new BufferedReader(new InputStreamReader(jogador1.getInputStream()));
            PrintWriter saidaJogador1 = new PrintWriter(jogador1.getOutputStream(), true);
            BufferedReader entradaJogador2 = new BufferedReader(new InputStreamReader(jogador2.getInputStream()));
            PrintWriter saidaJogador2 = new PrintWriter(jogador2.getOutputStream(), true);

            if (entradaJogador1 != null && entradaJogador2 != null){
                inicializarJogo(entradaJogador1, saidaJogador1, entradaJogador2, saidaJogador2);

            }
            else {
                System.out.println("Erro ao conectar os jogadores");
            }


            jogador1.close();
            jogador2.close();
        } catch (Exception e) {
            System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.iniciandoConexao();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}