package InterfaceSwing;
import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JOptionPane;

public class Server {
    private static final int porta = 8081;
    private static final String[] palavras = {"cachorro", "gato", "elefante", "girafa", "leao", 
    "tigre", "pato", "ganso", "galinha", "papagaio", "pomba", "coruja", "urubu", "pelicano", "avestruz",
    "geladeira", "fogao", "bandeira", "gravata", "camisa", "sapato", "lua", "planta", "arvore", "flor"};

    private static final String[] palavrasMedias = {"novembro", "palpite", "mistura", "quintal", "registro", "sujeito", "teclado"};

    private static final String[] palavrasDificeis = {"xenofobia", "inconstitucional", "paralelepipedo",
    "heterodoxo", "onomatopeia", "proparoxitona", "complexidade", "melancolia", "eclesiastico", "metamorfose",
    "tautologia" };

    private int maximasTentativas;
    private Clip clip;
    private Set<Character> letrasTentadas; /// caractere

    public Server(int maximasTentativas) {
        this.maximasTentativas = maximasTentativas;
        this.letrasTentadas = new HashSet<>();
    }

    public Server() {
        this.letrasTentadas = new HashSet<>();
    }

    // tocar som
    public void playSound(String filePath){
        try {
            if (clip != null && clip.isRunning()) {
                return;
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream); // abre o arquivo de audio
            clip.start();//começar
        } catch (Exception e) {
            System.out.println("Erro ao tocar o som: " + e.getMessage());
        }
    }

    // usa true ou false
    public int sortearDificuldade() {
        // media, facil e  dificil
        Random aleatorio = new Random();
        int dificuldade = aleatorio.nextInt(3);
        // f, m, d
        return dificuldade;
    }

    public String escolherPalavra(String[] palavra) {
        // implementação
        Random aleatorio = new Random();
        int indiceAleaorio = aleatorio.nextInt(palavra.length);
        return palavra[indiceAleaorio];
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
            String palavra = null;
            String dificuldade = null;
            StringBuilder letrasErradas = new StringBuilder();

            while ((mensagem = entradaJogador1.readLine()) != null) {
                if (mensagem.startsWith("Dificuldade: ")) {
                    dificuldade = mensagem.substring(13);
                    break;
                }
            }

            if (dificuldade != null) {
                switch (dificuldade) {
                    case "Difícil":
                        palavra = escolherPalavra(palavrasDificeis);
                        break;
                    case "Médio":
                        palavra = escolherPalavra(palavrasMedias);
                        break;
                    case "Fácil":
                        palavra = escolherPalavra(palavras);
                        break;
                }
            }

            String mascara = criarMascara(palavra);

            saidaJogador1.println("Bem-vindo ao jogo da forca! Primeiramente vez do Jogador 1 (Você)");
            saidaJogador1.println("Palavra: " + mascara);
            saidaJogador1.println("Dificuldade: " + dificuldade);
            saidaJogador1.println();
            saidaJogador2.println("Bem-vindo ao jogo da forca!");
            saidaJogador2.println("Palavra: " + mascara);
            saidaJogador2.println("Dificuldade: " + dificuldade);
            saidaJogador2.println();

            while (!mascara.equals(palavra) && mascara != null) {
                PrintWriter saidaAtual = (jogadorAtual == 1 ? saidaJogador1 : saidaJogador2);
                BufferedReader entradaAtual = (jogadorAtual == 1 ? entradaJogador1 : entradaJogador2);
                PrintWriter saidaOutro = (jogadorAtual == 1 ? saidaJogador2 : saidaJogador1);

                // Situação atual
                saidaAtual.println("Sua vez. Palavra: " + mascara);
                saidaAtual.println();
                saidaOutro.println();
                saidaAtual.println();
                saidaOutro.println();
                saidaAtual.println("Vez de jogador (Você) " + jogadorAtual);
                saidaOutro.println("Aguarde a vez do jogador " + jogadorAtual);

                mensagem = entradaAtual.readLine(); // le a mensagem do cliente
                if (mensagem.startsWith("Tentativa: ")) { // se a mensagem do cliente começar com "Tentativa: "
                    String tentativa = mensagem.substring(11);
                    char letraTentada = tentativa.charAt(0);

                    if (letrasTentadas.contains(letraTentada)) {
                        playSound("musicas/letraErrada.wav");
                        saidaAtual.println("Letra já digitada! ");
                    } else {
                        letrasTentadas.add(letraTentada);
                        if (palavra.contains(tentativa)) {
                            // Atualiza a máscara
                            for (int i = 0; i < palavra.length(); i++) {
                                if (palavra.charAt(i) == letraTentada) {
                                    mascara = mascara.substring(0, i) + letraTentada + mascara.substring(i + 1);
                                }
                            }
                            playSound("musicas/acertaPalavra.wav");
                            saidaAtual.println("Acertou! " + mascara);
                        } else {
                            playSound("musicas/letraErrada.wav");
                            letrasErradas.append(letraTentada).append(" ");
                            saidaAtual.println("Letra não encontrada!");
                        }
                    }

                    saidaAtual.println("Estado: " + mascara);
                    saidaAtual.println("Letras Erradas: " + letrasErradas.toString().toUpperCase());
                    saidaOutro.println("Estado: " + mascara);
                    saidaOutro.println("Letras Erradas: " + letrasErradas.toString().toUpperCase());

                    verificarVitoria(palavra, mascara, saidaAtual, entradaAtual, saidaOutro, jogadorAtual);

                    // Alterna o jogador atual
                    if (jogadorAtual == 1) {
                        jogadorAtual = 2;
                    } else {
                        jogadorAtual = 1;
                    }
                } else if (mensagem.startsWith("Chat: ")) {
                    String chat = mensagem.substring(6); // remove o termo "Chat: " da mensagem
                    saidaOutro.println("Chat do jogador " + jogadorAtual + ": " + chat);
                }
            }
        }
    }

    public void verificarVitoria(String palavra, String mascara, PrintWriter saidaAtual, 
        BufferedReader entradaAtual, PrintWriter saidaOutro, int jogadorAtual) {
        if (mascara.equals(palavra)) {
            playSound("musicas/vitoria.wav");
            JOptionPane.showMessageDialog(null, "Parabéns, jogador: " + jogadorAtual + " ! Você acertou a palavra: " + palavra);
            saidaAtual.println("Fim de jogo! Você venceu! ");
            saidaOutro.println("Fim de jogo! Você perdeu! ");
            playSound("musicas/jogoPerdido.wav");
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
            } else {
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