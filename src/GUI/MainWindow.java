package GUI;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import GraphXings.Algorithms.NewPlayer;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.Vector;
import GraphXings.Game.GuiGameResult;
import GraphXings.Game.NewGame;
import GraphXings.Game.NewGameResult;
import GraphXings.Game.GameInstance.*;

/**
 * The class mainWindows holds the frame of the mainwindow. It is implemented as a SINGLETON.
 */


public class MainWindow extends JFrame implements ActionListener, PropertyChangeListener{

    private static final MainWindow MWINDOW = new MainWindow();



    private MainWindow() {
        super("GraphXings Viewer");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        classes = new playerClasses();

        
        createMenu();
        createPanels();
        initializeSimulationData();
        

        this.pack();
        this.setVisible(true);
    }


    public static MainWindow getInstance()
    {
        return MWINDOW;
    }

    
    private void initializePlayers()
    {
        String player1Name = classes.name(player1Box.getSelectedIndex()) + " (1)";
        String player2Name = classes.name(player2Box.getSelectedIndex()) + " (2)";
        player1 = classes.createPlayer(player1Box.getSelectedIndex(),player1Name);
        player2 = classes.createPlayer(player2Box.getSelectedIndex(),player2Name);
    }


    private void initializeSimulationData()
    {  
        timeLimit = ((Long)timeLimitField.getValue()).longValue();
        randomizerSeed = ((Integer)randomizeField.getValue()).longValue();
        gameType = this.gameType();
        
    }

    private NewGame.Objective gameType()
    {
        return NewGame.Objective.CROSSING_NUMBER;
        
    }



    private void createMenu() 
    {
        this.menuBar = new JMenuBar();
        SimulationMenu = new JMenu("Simulation"); // You should provide a title for the JMenu
        JMenuItem addPlayerItem = new JMenuItem("Add Player");
        JMenuItem startSimItem = new JMenuItem("Start Simulation");

        SimulationMenu.add(addPlayerItem);
        SimulationMenu.add(startSimItem);

        this.menuBar.add(SimulationMenu);

        this.setJMenuBar(menuBar); // Set the menu bar to the JFrame
    }



    private void createConfigurationPanel()
    {
        // Main Config Panel
        configPanel = new JPanel();
        //configPanel.setPreferredSize(new Dimension(250,80));
        //configPanel.setAlignmentX(LEFT_ALIGNMENT);
        configPanel.setAlignmentX(LEFT_ALIGNMENT);
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.PAGE_AXIS));
        


        // *** Add Headline
        JPanel headlinePanel = new JPanel();
        headlinePanel.setLayout(new BoxLayout(headlinePanel, BoxLayout.LINE_AXIS));
        JLabel configHeadlinelabel = new JLabel("Configuration Panel");
        headlinePanel.add(configHeadlinelabel);
        configPanel.add(headlinePanel);
        configPanel.add(Box.createVerticalStrut(20));
    

        JPanel playersPanel = new JPanel();
        playersPanel.setLayout(new BoxLayout(playersPanel, BoxLayout.LINE_AXIS));
        
        String[] playerTypeNames = classes.names().toArray(new String[0]);
        player1Box = new JComboBox<String>(playerTypeNames);
        player2Box = new JComboBox<String>(playerTypeNames);
        initializePlayers();
        playersPanel.add(player1Box);
        playersPanel.add(new JLabel(" - "));
        playersPanel.add(player2Box);
        player1Box.addActionListener(this);
        player2Box.addActionListener(this);
        configPanel.add(playersPanel);
        configPanel.add(Box.createVerticalStrut(20));

        // Game Type
        JPanel gameTypePanel = new JPanel();
        gameTypePanel.setLayout(new BoxLayout(gameTypePanel, BoxLayout.LINE_AXIS));
        gameTypePanel.add(new JLabel("Type of Game: "));
        gameTypeBox = new JComboBox<String>();
        gameTypeBox.addItem("Crossing Number");
        gameTypeBox.addItem("Crossing Angle");
        gameTypeBox.addActionListener(this);
        gameTypePanel.add(gameTypeBox);
        configPanel.add(gameTypePanel);
        configPanel.add(Box.createVerticalStrut(20));


        // Time Limit
        JPanel timeLimitPanel = new JPanel();
        timeLimitPanel.setLayout(new BoxLayout(timeLimitPanel, BoxLayout.LINE_AXIS));

        NumberFormatter formatterTimeLimit = new NumberFormatter(NumberFormat.getIntegerInstance());
        formatterTimeLimit.setValueClass(Integer.class);
        formatterTimeLimit.setMinimum(1);
        
        
        timeLimitField = new JFormattedTextField(formatterTimeLimit);
        timeLimitField.setColumns(10); 
        timeLimitField.setValue(300l);
        
        timeLimitPanel.add(new JLabel("Time Limit in Seconds: "));
        timeLimitPanel.add(timeLimitField);
        timeLimitField.addPropertyChangeListener(this);

        configPanel.add(timeLimitPanel);
        configPanel.add(Box.createVerticalStrut(20));


        // Randomizer
        JPanel randomSeedPanel = new JPanel();
        randomSeedPanel.setLayout(new BoxLayout(randomSeedPanel, BoxLayout.LINE_AXIS));

        NumberFormatter formatterSeed = new NumberFormatter(NumberFormat.getIntegerInstance());
        formatterSeed.setValueClass(Integer.class);
        formatterSeed.setMinimum(1);
        
        randomizeField = new JFormattedTextField(formatterSeed);
        randomizeField.setColumns(10); 
        randomizeField.setValue(27081883);

        
        randomSeedPanel.add(new JLabel("Randomizer Seed: "));
        randomSeedPanel.add(randomizeField);
        randomizeField.addPropertyChangeListener(this);

        configPanel.add(randomSeedPanel);
        configPanel.add(Box.createVerticalStrut(20));



        // Start Sim Button
        JPanel startSimPanel = new JPanel();
        startSimPanel.setLayout(new BoxLayout(startSimPanel, BoxLayout.LINE_AXIS));
        startSimButton = new JButton("Start Simulation");
        
        startSimPanel.add(startSimButton);
        startSimButton.addActionListener(this);
        
        configPanel.add(startSimPanel);
        configPanel.add(Box.createVerticalStrut(20));
        
        // Vertical glue
        

        //configPanel.add(Box.createVerticalBox());
        
        
        controlPanel.setTopComponent(configPanel);
        
    }

    
    private void createSimulationPanel()
    {
        simulationPanel = new JPanel();

        simulationPanel.setPreferredSize(new Dimension(250,80));
        simulationPanel.setAlignmentX(LEFT_ALIGNMENT);
        simulationPanel.setLayout(new BoxLayout(simulationPanel, BoxLayout.PAGE_AXIS));

        // *** Add Headline
        JPanel headlinePanel = new JPanel();
        headlinePanel.setLayout(new BoxLayout(headlinePanel, BoxLayout.LINE_AXIS));
        JLabel configHeadlinelabel = new JLabel("Result Panel");
        headlinePanel.add(configHeadlinelabel);
        simulationPanel.add(headlinePanel);
        simulationPanel.add(Box.createVerticalStrut(20));



        // initialize Info-Labels
        /* 
        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.LINE_AXIS));
        firstPlayerLabel= new JLabel(player1.getName());
        secondPlayerLabel= new JLabel(player2.getName());
         
        playerPanel.add(firstPlayerLabel);
        playerPanel.add(new JLabel(" - "));
        playerPanel.add(secondPlayerLabel);

        simulationPanel.add(playerPanel);
        simulationPanel.add(Box.createVerticalStrut(20));
        */
        
        // Game won 
        JPanel gameWonPanel = new JPanel();
        gameWonPanel.setLayout(new BoxLayout(gameWonPanel, BoxLayout.LINE_AXIS));
        gameWonLabel = new JLabel("No Game played yet");
        gameWonPanel.add(gameWonLabel);
        simulationPanel.add(gameWonPanel);
        simulationPanel.add(Box.createVerticalStrut(20));
        
        
        
        
        // Switch Rounds Button
        JPanel switchRoundsPanel = new JPanel();
        switchRoundsPanel.setLayout(new BoxLayout(switchRoundsPanel, BoxLayout.LINE_AXIS));
        switchRoundsButton = new JButton("Switch Rounds");
        switchRoundsButton.setEnabled(false);
        switchRoundsButton.addActionListener(this);
        switchRoundsPanel.add(switchRoundsButton);
        simulationPanel.add(switchRoundsPanel);
        simulationPanel.add(Box.createVerticalStrut(700));
        controlPanel.setBottomComponent(simulationPanel);
        
    }


    private void createResultPanel()
    {
        resultPanel = new GraphPanel();

        resultPanel.setLayout( new FlowLayout());

        mainPanel.setRightComponent(resultPanel);
    }

    


    private void createPanels()
    {
        mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainPanel.setDividerLocation(0.3);

        controlPanel  = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        controlPanel.setDividerLocation(0.5);


        createConfigurationPanel();
        createResultPanel();
        createSimulationPanel();


        
        this.add(mainPanel);
        mainPanel.setLeftComponent(controlPanel);
        //mainPanel.revalidate();
        //mainPanel.repaint();
    }



    private void startSimulation()
    {
        System.out.printf("Starting Simulation with the following settings:\n");
        System.out.printf("Player 1: %s\n", player1.getName());
        System.out.printf("Player 2: %s\n", player2.getName());

        System.out.printf("Randomizer Seed: %d\n", randomizerSeed);
        System.out.printf("TimeLimit in seconds: %d\n", timeLimit);
        
        switch (gameType) {
            case CROSSING_NUMBER:
                System.out.printf("Game Type: CROSSING NUMBER\n");
                break;
            case CROSSING_ANGLE:
                System.out.printf("Game Type: CROSSING ANGLE\n");
                break;
            
            default:
                System.out.printf("Game Type: UNKNOWN\n");
                break;
        }


        results = new Vector<GuiGameResult>();

        factory = new PlanarGameInstanceFactory(randomizerSeed);
        
        runGame();

        System.out.printf("Game finished\n");
        showResult(0);
        switchRoundsButton.setEnabled(true);
    }
    




    private void runGame()
    {
        GameInstance gi = factory.getGameInstance();
        NewGame game = new NewGame(gi.getG(),gi.getWidth(),gi.getHeight(),player1,player2,gameType,timeLimit*1000000000);
        gameResult = game.play();

        showResult(0);
    }


    private void nextGame()
    {
        System.out.println("MainWindow::nextGame() called");
        showNextResult();
    }


    // EVENT HANDLER
    // Action Handling
    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == this.startSimButton)
        {
            startSimulation();
        }
        else if (event.getSource() == this.switchRoundsButton)
        {
            nextGame();
        }
        else if (event.getSource() == this.player1Box)
        {
            player1 = classes.createPlayer(player1Box.getSelectedIndex());
        }
        else if (event.getSource() == this.player2Box)
        {
            player2 = classes.createPlayer(player2Box.getSelectedIndex());
        }
        else if (event.getSource() == this.gameTypeBox)
        {
            if (this.gameTypeBox.getSelectedIndex() == 0)
            {
                this.gameType = NewGame.Objective.CROSSING_NUMBER;
            }
            else if (this.gameTypeBox.getSelectedIndex() == 1)
            {   
                this.gameType = NewGame.Objective.CROSSING_ANGLE;
            }
        }
    }


    // Property Change Handling

    public void propertyChange(PropertyChangeEvent event)
    {
        if (event.getSource() == this.timeLimitField)
        {
            try 
            {
                if (this.timeLimitField.getValue() instanceof Number)
                {
                    timeLimit = ((Number) this.timeLimitField.getValue()).longValue();
                }
                
            } 
            catch (ClassCastException ex) 
            {
                System.err.println("Error: timeLimitField should be an long value");
            }
              
        }
        else if (event.getSource() == this.randomizeField)
        {
            try 
            {
                if (this.randomizeField.getValue() instanceof Number)
                {
                    randomizerSeed = ((Number) this.randomizeField.getValue()).longValue();
                }
            } 
            catch (ClassCastException ex) 
            {
                System.err.println("Error: randomizeField should be an long value");
            }
              
        }
    }




    public void addGameResult(GuiGameResult result)
    {
        System.out.println("MainWindow::addGameResult(GuiGameResult result) called");

        results.add(result);

        
    }


    public void showResult()
    {
        if (results != null)
        {
            if (currentResult >= results.size())
            {
                currentResult = 0;
            }
        
            resultPanel.draw(results.elementAt(currentResult));
            gameWonLabel.setText(gameResult.announceResult());
        } 
    }

    public void showNextResult()
    {
        currentResult++;
        showResult();
    }

    public void showResult(int num)
    {
        if (currentResult>=0)
        {
            currentResult = num;
        }
        else
        {
            currentResult = 0;
        }
        
        showResult();
    }

    // simulation elements
    //ArrayList<NewPlayer> players;
    NewPlayer player1;
    NewPlayer player2;
    long timeLimit;
    long randomizerSeed;
    NewGame.Objective gameType;
    PlanarGameInstanceFactory factory;
    playerClasses classes;

    
    Vector<GuiGameResult> results;
    NewGameResult gameResult;
    int currentResult;

    // action elements

    private JButton startSimButton;
    private JButton switchRoundsButton;

    // changing elements
    private JFormattedTextField timeLimitField;
    private JFormattedTextField randomizeField;

    // ComboBoxes
    JComboBox<String> player1Box;
    JComboBox<String> player2Box;
    JComboBox<String> gameTypeBox;

    // gui elements
    private JMenuBar menuBar;
    private JMenu SimulationMenu;

    // result labels
    //private JLabel numCurrentGameLabel;
    //private JLabel firstPlayerLabel;
    //private JLabel secondPlayerLabel;
    private JLabel gameWonLabel;

    private JSplitPane mainPanel;
    private JSplitPane controlPanel;
    private JPanel configPanel;
    private GraphPanel resultPanel;
    private JPanel simulationPanel;
    //private JPanel sim
}
