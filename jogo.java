import java.awt.*;
import java.awt.event.*;
//import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import java.util.Random;
import javax.imageio.*;

class JogoBase extends JFrame {
    final int ANDA1 = 1;
    final int ANDA2 = 2;
    final int PARADO = 3;

    Image personagem[] = new Image[6];
    Image bolas[] = new Image[20];
    Image numeros[] = new Image[10];
    Image score, ryuk, fundo;
    Desenho des = new Desenho();

    int posX = 500, estado = PARADO, sinal = -1;
    int xRyuk = 0, velocidade = 20, num, n1 = 0, n2 = 0, value = 0;
    int[] xBola = new int[20], yBola = new int[20];
    Timer timer, timerRyuk, timerBola;

    int bola = -1;

    Random rand = new Random();

    class Desenho extends JPanel {

        Desenho() {
            try {
                setPreferredSize(new Dimension(1000, 600));
                ryuk = ImageIO.read(new File("images\\ryuk.png"));
                personagem[ANDA1] = ImageIO.read(new File("images\\cris-1.png"));
                personagem[ANDA2] = ImageIO.read(new File("images\\cris-2.png"));
                personagem[PARADO] = ImageIO.read(new File("images\\cris-parado.png"));
                fundo = ImageIO.read(new File("images\\fundo.png"));
                for (int i = 0; i < 10; i++)
                    numeros[i] = ImageIO.read(new File("images\\numbers\\" + i + ".png"));
                for (int i = 0; i < 20; i++)
                    bolas[i] = ImageIO.read(new File("images\\bola-de-ouro.png"));
                score = ImageIO.read(new File("images\\numbers\\score.png"));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "A imagem não pode ser carregada!\n" + e, "Erro",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(fundo, 0, 0, getSize().width, getSize().height, this);
            // Coloca as bolas

            for (int i = 0; i <= bola; i++)
                g.drawImage(bolas[i], xBola[i], yBola[i], getSize().width / 22, getSize().height / 15, this);

            // Personagem
            if (sinal == 1)
                g.drawImage(personagem[estado], posX, 380, getSize().width / 7, getSize().height / 3, this);
            else
                g.drawImage(personagem[estado], posX + 200, 380, -getSize().width / 7, getSize().height / 3, this);
            // Ryuk
            g.drawImage(ryuk, xRyuk, 0, getSize().width / 3, getSize().height / 3, this);
            // Pontos
            g.drawImage(score, 880, 20, getSize().width / 10, getSize().height / 10, this);
            for (int i = 0; i < 10; i++) {
                if (n1 == i)
                    g.drawImage(numeros[i], 885, 80, getSize().width / 22, getSize().height / 15, this);
                if (n2 == i)
                    g.drawImage(numeros[i], 930, 80, getSize().width / 22, getSize().height / 15, this);
            }
            Toolkit.getDefaultToolkit().sync();
        }
    }

    void andaDireita() {

        if (estado == ANDA2) {
            estado = ANDA1;
            posX += 10;
            repaint();
        } else if (estado == ANDA1) {
            posX += 10;
            estado = ANDA2;
            repaint();
        }
    }

    void andaEsquerda() {

        if (estado == ANDA2) {
            estado = ANDA1;
            posX -= 10;
            repaint();
        } else if (estado == ANDA1) {
            posX -= 10;
            estado = ANDA2;
            repaint();
        }
    }

    class TrataTeclas extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                estado = ANDA1;
                if (sinal == -1) {
                    posX += 50;
                    sinal = 1;
                }
                repaint();
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                estado = ANDA1;
                if (sinal == 1) {
                    posX -= 50;
                    sinal = -1;
                }
                repaint();
            } else if (e.getKeyCode() == KeyEvent.VK_P) {
                if (estado != PARADO)
                    posX += sinal * 40;
                estado = PARADO;
                repaint();
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                score();
            }
        }
    }

    public void andaRyuk() {
        xRyuk += velocidade;
        if (xRyuk <= -50 || xRyuk >= 700)
            velocidade *= -1;
    }

    public void colocaBola() {
        bola++;
        yBola[bola] = 150;
        xBola[bola] = xRyuk + 170;
    }

    public void desceBola() {
        for (int i = 0; i <= bola; i++) {
            yBola[i] += 20;
            if (yBola[i] >= 600) {
                for (int j = 0; j < bola; j++) {
                    xBola[j] = xBola[j + 1];
                    yBola[j] = yBola[j + 1];
                }
                bola--;
            }
        }
    }

    public void score() {
        value++;
        n1 = value / 10;
        n2 = value - n1 * 10;
        repaint();
    }

    JogoBase() {
        super("Trabalho");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(des);
        pack();
        setVisible(true);

        addKeyListener(new TrataTeclas());

        timerRyuk = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                num = rand.nextInt(100) + 1;
                if (num % 10 == 0)
                    colocaBola();

                desceBola();
                andaRyuk();
                repaint();
            }
        });

        timerRyuk.setRepeats(true);
        timerRyuk.start();

        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sinal == 1)
                    andaDireita();
                else
                    andaEsquerda();

                if (posX > 880) {
                    sinal = -1;
                    estado = PARADO;
                    posX = 840;
                } else if (posX < -80) {
                    sinal = 1;
                    estado = PARADO;
                    posX = -40;
                }

                for (int i = 0; i <= bola; i++) {
                    int x;
                    if (sinal == -1)
                        x = posX + 100;
                    else
                        x = posX + 50;
                    if (yBola[i] >= 365 && x <= xBola[i] + 50 && x >= xBola[i] - 50) {
                        score();
                        for (int j = i; j < bola; j++) {
                            xBola[j] = xBola[j + 1];
                            yBola[j] = yBola[j + 1];
                        }
                        bola--;
                        if(i==0)
                            i = -1;
                    }
                }


            }
        });
        timer.start();
    }

    static public void main(String[] args) {
        JogoBase f = new JogoBase();
    }
}
