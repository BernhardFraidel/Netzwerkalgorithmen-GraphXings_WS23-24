package GUI;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

public class AddPlayerDialog extends JDialog implements ActionListener
{
    public AddPlayerDialog(JFrame owner)
    {
        super(owner, "Add New Player", true);
        classes = new playerClasses();

        returnValue = -1;
        panel = new JPanel(new FlowLayout());

        initComboBox();
        initButtons();

        this.add(panel);
        this.setSize(320, 240);
        this.setModal(true);

        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComboBox()
    {
        playerClassesBox = new JComboBox<String>();

        for (int i=0; i< classes.size(); i++)
        {
            playerClassesBox.addItem (classes.name(i));
        }

        panel.add(playerClassesBox);
    }

    private void initButtons()
    {
        addPlayerButton = new JButton("Add Player");
        cancelButton = new JButton("Cancel");

        panel.add(addPlayerButton);
        panel.add(cancelButton);

        addPlayerButton.addActionListener(this);
        cancelButton.addActionListener(this);
    }


        int showDialog() 
        {
            this.setVisible(true);
            return returnValue;
    }


    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == this.addPlayerButton)
        {
            returnValue = classes.type(playerClassesBox.getSelectedItem().toString());
        }
        else
        {
            returnValue = -1;
        }

        this.setVisible(false);
        this.dispose();
    }


    private playerClasses classes;

    JPanel panel;
    JComboBox<String> playerClassesBox;
    JButton addPlayerButton;
    JButton cancelButton;

    int returnValue;
}
