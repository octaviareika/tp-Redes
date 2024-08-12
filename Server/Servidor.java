package Server;
import java.io.*;
import java.net.*;
import java.util.Random;
//import java.util.Scanner;

// Jogo da Forca

public class Servidor {
    private static final int porta = 8081;
    private static final String[] palavras = {"cachorro", "gato", "elefante", "girafa", "leao", 
    "tigre", "pato", "ganso", "galinha", "papagaio", "pomba", "coruja", "urubu", "pelicano", "avestruz",
    "geladeira", "fogao", "bandeira", "gravata", "camisa", "sapato", "lua", "planta", "arvore", "flor"};

    private static final String[] palavrasDificeis = {"xenofobia", "inconstitucional", "paralelepipedo",
    "heterodoxo", "onomatopeia", "proparoxitona", "complexidade", "melancolia", "eclesiastico", "metamorfose",
    "tautologia" };
    private int maximasTentativas;

    public Servidor(int maximasTentativas){
        this.maximasTentativas = maximasTentativas;
    }

    public Servidor(){}

    // usa true ou false
    public boolean sortearDificuldade(){
        Random aleatorio = new Random();
        return aleatorio.nextBoolean();
    }
    
    public String escolherPalavra(String[] palavra) {
        Random aleatorio = new Random();
        int indiceAleaorio = aleatorio.nextInt(palavra.length);

        return palavras[indiceAleaorio];
    }

    public int sortearTentativaFacil(){
        Random random = new Random();
        this.maximasTentativas = random.nextInt(3) + 3;
        return this.maximasTentativas;
    }

    public int sortearTentativaDificil(){
        Random random = new Random();
        this.maximasTentativas = random.nextInt(3) + 6;
        return this.maximasTentativas;
        
    }

    // getter
    public int getMaximoTentativas(){
        return maximasTentativas;
    }

    // criar uma mascara para a palavra

    public String criarMascara(String palavra){
        String mascara = "";

        for (int i =0; i < palavra.length(); i++){
            mascara += "_";
        }

        return mascara;
    }

    public void limparTela() {
        // Código de escape ANSI para limpar a tela
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void inicializarJogo(BufferedReader entradaJogador1, PrintWriter saidaJogador1, 
    BufferedReader entradaJogador2, PrintWriter saidaJogador2  ) throws IOException {
        String palavra;
        String dificuldade;

        if (sortearDificuldade()){ // se for dificil 
            palavra = escolherPalavra(palavrasDificeis);
            this.maximasTentativas = sortearTentativaDificil();
            dificuldade = "Dificil";
        }
        
        else {
            palavra = escolherPalavra(palavras);
            this.maximasTentativas = sortearTentativaDificil();
            dificuldade = "Fácil";
        }


        String mascara = criarMascara(palavra);
        
        int tentativas = 0;
        int jogadorAtual = 1;
        String letrasDigitadas = "";


        while (!mascara.equals(palavra) && mascara != null && tentativas < this.getMaximoTentativas()) {
            //limparTela();
            
            PrintWriter saidaAtual = (jogadorAtual == 1) ? saidaJogador1 : saidaJogador2;
            BufferedReader entradaAtual = (jogadorAtual == 1) ? entradaJogador1 : entradaJogador2;
            PrintWriter saidaOutro = (jogadorAtual == 1) ? saidaJogador2 : saidaJogador1;
            
            boolean letraEstaNaPalavra = false;

            saidaAtual.println("Sua vez. Palavra: " + mascara + " Letras digitadas: " + letrasDigitadas);
            saidaAtual.println("Dificuldade" + dificuldade);
            saidaAtual.println("Vez de jogador " + jogadorAtual);
            saidaOutro.println("Aguarde a vez do jogador" + jogadorAtual);
            
            
            String tentativa = entradaAtual.readLine();
            if ( tentativa != null && tentativa.length() == 1){ 
                char letra = tentativa.charAt(0);

                

                // verificar se a letra ja foi digitada
                if (letrasDigitadas.indexOf(letra) == -1){
                    letrasDigitadas += letra + " "; // concatena a letra nas que ja foram digitadas
                    boolean acertou = false;
                    
                    // se a letra não foi digitada mas está na palavra
                    for (int i = 0; i < palavra.length(); i++){
                        if (palavra.charAt(i) == letra){
                            mascara = mascara.substring(0, i) + letra + mascara.substring(i + 1);
                            letraEstaNaPalavra = true;
                            acertou = true;
                        }
                    }

                    if (!acertou){
                        tentativas++;
                        saidaAtual.println("Letra não encontrada. Tentativas restantes: " + (this.getMaximoTentativas() - tentativas));
                    } else {
                        saidaAtual.println("Letra encontrada!");
                    }


                    jogadorAtual = (jogadorAtual == 1) ? 2 : 1;
                } else {
                    saidaAtual.println("Letra já digitada. Tente outra letra.");
                }

            }


                if (mascara.equals(palavra)){
                    saidaAtual.println("Parabéns! Você acertou a palavra: " + palavra + "Fim de jogo");
                    saidaOutro.println("Você perdeu! A palavra era: " + palavra + "Fim de jogo");
                }

                else if (tentativas >= this.getMaximoTentativas()){
                    saidaAtual.println("Você perdeu! A palavra era: " + palavra);
                    saidaOutro.println("Parabéns! Você acertou a palavra: " + palavra);
                }

                limparTela();
                
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

    public static void main(String[] args) throws IOException {
        Servidor servidor = new Servidor(10);
        servidor.iniciandoConexao();
    }
}
