package InterfaceSwing;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Client extends JFrame {
    JTextField letraInput;
    private JTextArea chatArea;
    private JTextField chatInput;
    private JLabel estadoLabel;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    public Client() {
        setTitle("Jogo da Forca");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // fecha a janela

        estadoLabel = new JLabel("Estado: ");
        letraInput = new JTextField(10);
        chatArea = new JTextArea(10, 30);
        chatArea.setEditable(false);
        chatInput = new JTextField(20);

        

        JButton enviarLetra = new JButton("Enviar letra");
        enviarLetra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String letra = letraInput.getText();
                if (!letra.isEmpty()) {
                    out.println("Tentativa: " + letra);
                    letraInput.setText(""); // faz ficar em branco dvnv
                }

                else {
                    JOptionPane.showMessageDialog(null, "Digite uma letra para enviar.");
                }
            }
        });

        JButton enviarChatButton = new JButton("Enviar Chat");
        enviarChatButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String chat = chatInput.getText().trim();
                if (!chat.isEmpty()) {
                    out.println("Chat: " + chat);
                    chatInput.setText("");
                }
                
                else {
                    JOptionPane.showMessageDialog(null, "Digite uma mensagem para enviar.");
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(estadoLabel);
        topPanel.add(new JLabel("Letra:"));
        topPanel.add(letraInput);
        topPanel.add(enviarLetra);

        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel chatInputPanel = new JPanel();
        chatInputPanel.add(new JLabel("Chat:"));
        chatInputPanel.add(chatInput);
        chatInputPanel.add(enviarChatButton);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(chatPanel, BorderLayout.CENTER);
        panel.add(chatInputPanel, BorderLayout.SOUTH);

        add(panel);

        try {
            socket = new Socket("localhost", 8081); // socket do cliente conecta-se com o servidor
            out = new PrintWriter(socket.getOutputStream(), true); // envia mensagens para o servidor
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // recebe mensagens do servidor

            new Thread(new Runnable() { // o que essa thread faz? ela fica lendo as mensagens do servidor
                public void run() {
                    try {
                        String message;
                        while ((message = in.readLine()) != null) {
                            if (message.startsWith("Estado: ")) {
                                estadoLabel.setText(message);
                            } else if (message.startsWith("Chat: ")) {
                                chatArea.append(message.substring(6) + "\n");
                            } else {
                                chatArea.append(message + "\n");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        dispose();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        out.println("Sair");
        super.dispose();

        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Client client = new Client();
        client.setVisible(true);
    }
}