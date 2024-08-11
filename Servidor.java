import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.Scanner;

// Jogo da Forca

public class Servidor {
    private static final int porta = 8081;
    private static final String[] palavras = {"cachorro", "gato", "elefante", "girafa", "leao", 
    "tigre", "pato", "ganso", "galinha", "papagaio", "pomba", "coruja", "urubu", "pelicano", "avestruz",
    "geladeira", "fogao", "bandeira", "gravata", "camisa", "sapato", "lua", "planta", "arvore", "flor"};
    private int maximasTentativas;

    public Servidor(int maximasTentativas){
        this.maximasTentativas = maximasTentativas;
    }

    public Servidor(){}
    
    public String escolherPalavra() {
        Random aleatorio = new Random();
        int indiceAleaorio = aleatorio.nextInt(palavras.length);

        return palavras[indiceAleaorio];
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

    public void inicializarJogo() throws IOException {
        String palavra = escolherPalavra();
        String mascara = criarMascara(palavra);
        Scanner scanner = new Scanner(System.in);
        int tentativas = 0;

        while (mascara != palavra && tentativas <= this.getMaximoTentativas()) {
            // letras que ja foram digitadas
            
            String letrasDigitadas = "";
            boolean letraEstaNaPalavra = false;

            mostrarSituaçãoJogo(mascara);

            System.out.println("Digite uma letra: ");
            char letra = scanner.next().charAt(0);

            // verificar se a letra esta na palavra
            for (int i = 0; i < palavra.length(); i++){
                if (palavra.charAt(i) == letra){
                    mascara = mascara.substring(0, i) + letra + mascara.substring(i + 1);
                    letraEstaNaPalavra = true;
                }
            }

            // verificar se a letra ja foi digitada
            if (letrasDigitadas.indexOf(letra) == -1){
                letrasDigitadas += letra + " "; // concatena a letra nas que ja foram digitadas
                tentativas++;
            }

            else {
                System.out.println("Você ja digitou essa letra antes");
            }

            if (!letraEstaNaPalavra){
                System.out.println("Letra errada");
            }

            if (mascara.equals(palavra)){
                System.out.println("Parabéns, você acertou a palavra!");
            }

            else {
                System.out.println("Você perdeu! A palavra era: " + palavra);
            }
        }

        scanner.close();
        
    }

    public void mostrarSituaçãoJogo(String mascara) {
        // mostrar a palavra com a mascara
        // mostrar as letras erradas
        // mostrar as partes do boneco

        System.out.println("Situação do jogo");
        System.out.println("Palavra: " + mascara);
        System.out.println("Tamanho: " + mascara.length());
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


            inicializarJogo();
        }
    }
}
