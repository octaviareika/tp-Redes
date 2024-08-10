import java.io.*;
import java.net.*;
import java.util.Random;

// Jogo da Forca

public class Servidor {
    private static final int porta = 8081;
    private static final String[] palavras = {"cachorro", "gato", "elefante", "girafa", "leao", 
    "tigre", "pato", "ganso", "galinha", "papagaio", "pomba", "coruja", "urubu", "pelicano", "avestruz",
    "geladeira", "fogao", "bandeira", "gravata", "camisa", "sapato", "lua", "planta", "arvore", "flor"};

    public String escolherPalavra() {
        Random aleatorio = new Random();
        int indiceAleaorio = aleatorio.nextInt(palavras.length);

        return palavras[indiceAleaorio];
    }

    // criar uma mascara para a palavra

    public String criarMascara(String palavra){
        String mascara = "";

        for (int i =0; i < palavra.length(); i++){
            mascara += "_";
        }

        return mascara;
    }

    public void iniciandoConexao() throws IOException {
        try (ServerSocket socket = new ServerSocket(porta)){
            System.out.println("Servidor iniciado na porta " + porta);
            System.out.println("Aguardando conexão de jogadores...");

            Socket jogador1 = socket.accept();
            System.out.println("Jogador 1 conectado!");

            Socket jogador2 = socket.accept();
            System.out.println("Jogador 2 conectado!");
        }
    }
}
