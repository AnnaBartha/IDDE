package edu.bbte.idde.baim2115.desktop;

import edu.bbte.idde.baim2115.backend.repository.IngatlanMuveletek;
import edu.bbte.idde.baim2115.backend.model.Ingatlan;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


public class IngatlanFrame {
    private final IngatlanMuveletek ingatlanMuveletek = IngatlanMuveletek.getInstance();

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(IngatlanFrame.class);

    public IngatlanFrame() {
        JFrame frame = new JFrame("Ingatlan hirdetesek");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(50, 50));
        panel.setBackground(Color.LIGHT_GRAY);


        frame.add(panel, BorderLayout.CENTER);

        JButton createButton = new JButton("CREATE");
        JButton updateButton = new JButton("UPDATE");
        JButton deleteButton = new JButton("DELETE");
        panel.add(createButton);
        panel.add(updateButton);
        panel.add(deleteButton);

        JTextArea textArea = new JTextArea(30, 50);
        panel.add(textArea);
        textArea.append("Orszag Varos Negyzetmeter Ar TulajNeve Elerhetoseg TelSzam\n");

        String orszag = "Romania";
        String varos = "Kolozsvar";
        Integer negyzetmeter = 25;
        Integer termekAra = 10;
        String tulajNeve = "Anna";
        String elerhetoseg = "11287";
        Ingatlan ingatlan = new Ingatlan(orszag, varos, negyzetmeter, termekAra, tulajNeve, elerhetoseg);
        Ingatlan ingatlanokCreate = ingatlanMuveletek.createIngatlan(ingatlan);
        String newInfo = ingatlanokCreate.getOrszag()
                + " "
                + ingatlanokCreate.getVaros()
                + " "
                + ingatlanokCreate.getNegyzetmeter()
                + " "
                + ingatlanokCreate.getTermekAra()
                + " "
                + ingatlanokCreate.getTulajNeve()
                + " "
                + ingatlanokCreate.getElerhetoseg()
                + "\n";

        textArea.append(newInfo);

        // CREATE eseménykezelő
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String orszag = JOptionPane.showInputDialog("Kerem az orszagot");
                String varos = JOptionPane.showInputDialog("Kerem a varost");
                Integer negyzetmeter = Integer.parseInt(JOptionPane.showInputDialog("Kerem a negyzetmetert"));
                Integer termekAra = Integer.parseInt(JOptionPane.showInputDialog("kerem az arat"));
                String tulajNeve = JOptionPane.showInputDialog("Kerem a tulaj nevet");
                String elerhetoseg = JOptionPane.showInputDialog("Kerem a tulaj telefonszamat");

                Ingatlan ingatlan = new Ingatlan(orszag, varos, negyzetmeter, termekAra, tulajNeve, elerhetoseg);
                Ingatlan ingatlanokCreate = ingatlanMuveletek.createIngatlan(ingatlan);
                List<Ingatlan> listazas = ingatlanMuveletek.readIngatlan();
                LOGGER.info("CREATE eredmenye list: " + listazas);
                LOGGER.info("a letrehozott inbgatlan id-ja:" + ingatlanokCreate.getId());

                LOGGER.info("CREATE eredmenye: " + ingatlanokCreate);
                String newInfo = ingatlanokCreate.getOrszag()
                        + " "
                        + ingatlanokCreate.getVaros()
                        + " "
                        + ingatlanokCreate.getNegyzetmeter()
                        + " "
                        + ingatlanokCreate.getTermekAra()
                        + " "
                        + ingatlanokCreate.getTulajNeve()
                        + " "
                        + ingatlanokCreate.getElerhetoseg()
                        + "\n";

                textArea.append(newInfo);

            }
        });

        //        // UPDATE eseménykezelő
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String orszag = JOptionPane.showInputDialog("Kerem az orszagot");
                String varos = JOptionPane.showInputDialog("Kerem a varost");
                Integer negyzetmeter = Integer.parseInt(JOptionPane.showInputDialog("Kerem a negyzetmetert"));
                Integer termekAra = Integer.parseInt(JOptionPane.showInputDialog("kerem az arat"));
                String tulajNeve = JOptionPane.showInputDialog("Kerem a tulaj nevet");
                String elerhetoseg = JOptionPane.showInputDialog("Kerem a tulaj telefonszamat");
                Long id = (long) Integer.parseInt(JOptionPane.showInputDialog("Kerem a modositani kivant id-t"));

                Ingatlan ingatlanok = new Ingatlan(orszag, varos, negyzetmeter, termekAra, tulajNeve, elerhetoseg);
                Ingatlan ingatlanokUpdate = ingatlanMuveletek.updateIngatlan(ingatlanok, id);
                LOGGER.info("UPDATE eredmenye: " + ingatlanokUpdate);

                textArea.setText("");
                textArea.append("Orszag Varos Negyzetmeter Ar TulajNeve Elerhetoseg TelSzam\n");

                for (Ingatlan ingatlan : ingatlanMuveletek.readIngatlan()) {
                    textArea.append(ingatlan.getOrszag()
                            + " "
                            + ingatlan.getVaros()
                            + " "
                            + ingatlan.getNegyzetmeter()
                            + " "
                            + ingatlan.getTermekAra()
                            + " "
                            + ingatlan.getTulajNeve()
                            + " "
                            + ingatlan.getElerhetoseg()
                            + "\n");

                }
            }
        });

        // DELETE eseménykezelő
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Long id = (long) Integer.parseInt(JOptionPane.showInputDialog("Kerem a torolni kivant id-t"));
                int valasz = JOptionPane.showConfirmDialog(null,
                        "Biztosan torolni szeretne a(z) "
                                + id
                                + ". ingatlant?", "Torles megerositese",
                        JOptionPane.YES_NO_OPTION);
                if (valasz == JOptionPane.YES_OPTION) {
                    ingatlanMuveletek.deleteIngatlan(id);
                    textArea.setText("");
                    textArea.append("Orszag Varos Negyzetmeter Ar TulajNeve Elerhetoseg TelSzam\n");
                    for (Ingatlan ingatlan : ingatlanMuveletek.readIngatlan()) {
                        textArea.append(ingatlan.getOrszag()
                                + " "
                                + ingatlan.getVaros()
                                + " "
                                + ingatlan.getNegyzetmeter()
                                + " " + ingatlan.getTermekAra()
                                + " "
                                + ingatlan.getTulajNeve()
                                + " "
                                + ingatlan.getElerhetoseg()
                                + "\n");
                    }
                }
            }
        });
        frame.setVisible(true);
    }

}
