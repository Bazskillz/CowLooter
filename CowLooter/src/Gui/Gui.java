package CowLooter.src.Gui;

import javax.swing.*;

public class Gui extends JFrame {

    public Gui(CowLooter.src.Main.Main reference) {
        init(reference);
    }

    public void init(CowLooter.src.Main.Main reference) {

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        reference.isStarted = true;
        setVisible(false);
    }
}
