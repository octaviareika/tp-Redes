package Cliente;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {

    public static void limparTela() {
        // Código de escape ANSI para limpar a tela
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    public static void main(String[] args) throws Exception {
        // stamos criando uma nova instância de Socket que tenta se conectar ao servidor que está rodando no localhost (127.0.0.1) na porta 8081
        try (Socket socket = new Socket("localhost", 8081)){ // 8081 é a porta onde o servidor esta ouvindo
            // lê dados do fluxo de entrada do socket. Isso permite que o cliente receba mensagens do servidor.
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // lê dados do fluxo de saída do socket. Isso permite que o cliente envie mensagens para o servidor.
            PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
            // lê dados do teclado. Isso permite que o cliente leia mensagens do teclado.
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Conectado ao servidor");

            String mensagemServer; // mensagem recebida do servidor
        // enquanto a mensagem do servidor n for nula
            while ((mensagemServer = entrada.readLine()) != null){
                
                System.out.println(mensagemServer); // recebe mensagem do servidor
                if (mensagemServer.startsWith("Sua vez.")){ // se a mensagem do servidor começar com "Sua vez."
                    System.out.println("Digite uma letra ou envie uma mensagem de chat (Use 'Chat: ' antes da mensagem)");
                    String mensagem = teclado.readLine();
                    if (mensagem.startsWith("Chat: ")) { 
                        System.out.println();
                        saida.println("Chat: " + mensagem);
                        System.out.println();
                    } else {
                        saida.println("Tentativa: " + mensagem);
                    }
                }

                if (mensagemServer.startsWith("Parabéns!")){
                    break;
                }

                if (mensagemServer.startsWith("Você perdeu!")){
                    break;
                }
              
            }

        } catch (Exception erro){
            System.out.println("Erro ao conectar ao servidor: " + erro.getMessage());
        }
    }
}
