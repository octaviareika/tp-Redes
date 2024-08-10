import java.io.*;
import java.net.*;

/*1) Servidor: Responsável por gerenciar a palavra secreta e o estado do jogo (letras adivinhadas, letras erradas, partes do boneco desenhadas). De uma lista inicial de palavras do português, o servidor escolhe uma palavra aleatóriamente.

2) Clientes: Jogadores que se conectam ao servidor para tentar adivinhar a palavra.

3) Comunicação: Utilização de sockets TCP para comunicação entre cliente e servidor.

4) Fluxo do Jogo: O servidor inicializa e espera conexões de dois jogadores. 
O jogador digita no IP e a PORTA para se conectar ao servidor. Quando dois jogadores se 
conectam, o jogo deve começar. O servidor escolhe uma palavra e envia a palavra para os 
jogadores conectados e informa qual jogador deve fazer a tentativa. O servidor deve alternar 
a vez entre cada jogador. Na sua vez, o jogador digita uma letra e envia a tentativa para o servidor. 
O servidor verifica se a letra está presente na palavra e notifica ambos jogadores. Ao notificar ambos 
jogadores, o servidor envia a palavra, que pode estar parcialmente preenchida e as letras que já foram ]
selecionadas. O jogo termina quando um jogador acerta a palavra. */
public class Main {

    
}