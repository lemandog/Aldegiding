package com.lemandog;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

import static com.lemandog.Main.*;

public class Main {
static boolean counting = false;
static ImageIcon alde = new ImageIcon(Objects.requireNonNull(Main.class.getResource("1.png")));
static Color backg = new Color(45,97,211);
static double alpha = 0; //degree
static Thread counter; // To avoid multiple threads running at once
static public DateTimeFormatter sdfF = DateTimeFormatter.ofPattern("HH:mm:ss").withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault()); //format standard
static JLabel distanceT;
static long dist = 0;
    public static void main(String[] args) {
        JFrame main = new JFrame("ALDEGIDING V0.823.17.5a");
        main.setIconImage(alde.getImage());
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setLayout(new BorderLayout());
        main.setSize(500,300);
        main.setMinimumSize(new Dimension(500,300));
        JButton start = new JButton("START ALDEGIDING");
        JLabel timeElapsedL = new JLabel("YOU HAVE BEEN ALDEGIDING FOR: 00:00:00", SwingConstants.CENTER);
        distanceT = new JLabel("DISTANCE TRAVELED: "+ dist, SwingConstants.CENTER);
        timeElapsedL.setOpaque(true); // to set background it should be opaque. Refer to javaDocs. http://java.sun.com/javase/6/docs/api/javax/swing/JComponent.html#setOpaque%28boolean%29
        timeElapsedL.setBackground(backg);
        timeElapsedL.setForeground(Color.WHITE);
        distanceT.setOpaque(true); // to set background it should be opaque. Refer to javaDocs. http://java.sun.com/javase/6/docs/api/javax/swing/JComponent.html#setOpaque%28boolean%29
        distanceT.setBackground(backg);
        distanceT.setForeground(Color.WHITE);
        start.setBackground(backg);
        start.setForeground(Color.WHITE);
        JPanel stats = new JPanel();
        stats.setOpaque(true);
        stats.setBackground(backg);
        Aldegida mainAlde = new Aldegida();
        start.addActionListener(e -> {
            counting = !counting;
            if(counting) {
                counter = new Thread(() -> {
                    Instant timeStarted = Instant.now();
                    long step = 0;
                    long nextStepMax = 600;
                    mainAlde.speedReset();
                    start.setText("STOP ALDEGIDING");
                    mainAlde.timer.start();
                    while (counting) {
                        try {
                            step++;
                            if (step > nextStepMax) {
                                step = 0;
                                mainAlde.addTospeed();
                            }
                            timeElapsedL.setText("YOU HAVE BEEN ALDEGIDING FOR: "
                                    + sdfF.format(LocalDateTime.ofEpochSecond(Instant.now().getEpochSecond()
                                    - timeStarted.getEpochSecond(), 0, ZoneOffset.UTC)));
                            distanceT.setText("DISTANCE TRAVELED: "+ dist + " px");
                            Thread.sleep(200);
                        } catch (InterruptedException ignore) {} // Could be "sleep interrupted" exception. It doesn't matter here.
                    }

                });
                    counter.start();
            }else{
                if (counter.isAlive()) {counter.interrupt();}
                mainAlde.timer.stop();
                start.setText("START ALDEGIDING");
            }});
        stats.add(timeElapsedL);
        stats.add(distanceT);
        main.add(stats,BorderLayout.NORTH);
        main.add(mainAlde,BorderLayout.CENTER);
        main.add(start,BorderLayout.SOUTH);
        main.setVisible(true);
    }
}
class Aldegida extends JPanel{
    int x = 0;
    int y = 0;
    public Timer timer;
    public int addictiveX = 1;
    public int addictiveY = 1;
    public double alphaRot = 0.5;
    public Aldegida() {
        timer = new Timer(40, e -> {
            x += addictiveX;
            y += addictiveY;
            alpha += alphaRot;
            dist += (long)Math.sqrt(Math.pow(addictiveX,2) + Math.pow(addictiveY,2));
            if (x > getWidth()- alde.getIconWidth() || x < 0) {
                addictiveX = -addictiveX;
                alphaRot = -alphaRot;
            }
            if (y > getHeight()-alde.getIconHeight()  || y < 0) {
                addictiveY = -addictiveY;
                alphaRot = -alphaRot;
            }
            repaint();
        });
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        AffineTransform at = new AffineTransform();
        at.translate(x,y);
        at.rotate(Math.toRadians(alpha), (double) alde.getIconWidth() / 2, (double) alde.getIconHeight() / 2);
        setBackground(backg);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(alde.getImage(),at,null);
    }

    public void addTospeed() {
        if(addictiveX>0){addictiveX += 1;} else {addictiveX -= 1;}
        if(addictiveY>0){addictiveY += 1;} else {addictiveY -= 1;}
        if(alphaRot>0){alphaRot += 0.2;} else {alphaRot -= 0.2;}
    }
    public void speedReset() {
        if(addictiveX>0){addictiveX = 1;} else {addictiveX = -1;}
        if(addictiveY>0){addictiveY = 1;} else {addictiveY = -1;}
        if(alphaRot>0){alphaRot = 0.5;} else {alphaRot = -0.5;}
    }
}
