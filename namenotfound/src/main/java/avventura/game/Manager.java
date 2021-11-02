package game;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import engine.GameDescription;
import engine.launcher.Launcher;

public class Manager extends JFrame {
	private final Action newGameAction = new NewGame();
	private final Action loadAction = new Load();
	private final Action actionStart = new Start();
	private final Action actionBack = new Back();
	private JLayeredPane layeredPane = new JLayeredPane();
	private Map<String, GameDescription> saves = new HashMap<String, GameDescription>();
	private HandleDB db;
	private GameDescription selected = null;
	private final Action deleteAction = new Delete();
	public static Locale locale = Launcher.locale;
	ResourceBundle bundle;

	JPanel newGamePanel = new JPanel();
	JPanel mainPanel = new JPanel();
	JPanel langPanel = new JPanel();
	String player;


	/**
	 * Create the panel.
	 */
	public Manager() throws Exception{
		try {
			bundle = ResourceBundle.getBundle("manager", locale);

		}catch (MissingResourceException e) {
			bundle = ResourceBundle.getBundle("manager", Locale.ENGLISH);
		}

		db = new HandleDB();
		saves = db.recoveryTuple();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 475, 300);
		getContentPane().add(layeredPane);

		//init mainPanel
		this.setTitle(bundle.getString("manager.title"));
		mainPanel.setSize(414, 239);
		mainPanel.setLocation(10, 11);
		layeredPane.setLayer(mainPanel, 1);
		layeredPane.add(mainPanel);
		mainPanel.setLayout(null);


		JButton btnNewButton = new JButton(bundle.getString("manager.newgame"));
		btnNewButton.setAction(newGameAction);
		btnNewButton.setBounds(24, 205, 93, 23);
		mainPanel.add(btnNewButton);

		JButton btnNewButton_1 = new JButton(bundle.getString("manager.load"));
		btnNewButton_1.setAction(loadAction);
		btnNewButton_1.setBounds(127, 205, 67, 23);
		mainPanel.add(btnNewButton_1);

		JButton btnNewButton_2 = new JButton(bundle.getString("manager.delete"));
		btnNewButton_2.setAction(deleteAction);
		btnNewButton_2.setBounds(204, 205, 89, 23);
		mainPanel.add(btnNewButton_2);

		//init lista dei salvataggi
		JList list = new JList();
		list.setBounds(52, 40, 299, 157);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setModel(new DefaultListModel<String>());
		DefaultListModel<String> m = (DefaultListModel) list.getModel();
		mainPanel.add(list);
		mainPanel.setVisible(true);
		setVisible(true);
		layeredPane.setLayer(newGamePanel, 1);
		for(String k : saves.keySet())
			m.addElement(k);

		JLabel thumb = new JLabel(new javax.swing.ImageIcon(getClass().getResource("/header_manager.png")));
		thumb.setBounds(52,0,299,41);
		mainPanel.add(thumb);

		JButton lang = new JButton(bundle.getString("manager.lang"));
		lang.setBounds(303, 205, 93, 23);
		mainPanel.add(lang);
		lang.addActionListener(new Language());

		//init newGamePanel
		layeredPane.add(newGamePanel);
		newGamePanel.setSize(250, 200);
		newGamePanel.setLocation(10, 11);
		newGamePanel.setLayout(null);
		JLabel label = new JLabel("Name");
		label.setBounds(45, 20, 47, 14);
		newGamePanel.add(label);
		JTextField textField = new JTextField();
		textField.setBounds(100, 20, 133, 20);
		newGamePanel.add(textField);
		textField.setColumns(10);
		JButton startButton = new JButton(bundle.getString("manager.start"));
		JButton backButton = new JButton(bundle.getString("manager.back"));
		startButton.setBounds(50, 90, 80, 23);
		backButton.setBounds(160, 90, 71, 23);
		newGamePanel.add(backButton);
		backButton.setAction(actionBack);
		startButton.setAction(actionStart);


		newGamePanel.add(startButton);
		newGamePanel.setVisible(false);

		//lang panel
		layeredPane.add(langPanel);
		langPanel.setSize(250, 200);
		langPanel.setLocation(10, 11);
		langPanel.setLayout(null);
		langPanel.setVisible(false);
		JList langs = new JList();
		langs.setBounds(52, 40, 299, 157);
		langs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		langs.setModel(new DefaultListModel<String>());
		DefaultListModel<String> ml = (DefaultListModel) langs.getModel();
		langPanel.add(langs);
		ml.addElement("Italiano");
		ml.addElement("Inglese");


	}

	private class NewGame extends AbstractAction {
		public NewGame() {
			putValue(NAME, bundle.getString("manager.nameNew"));
			putValue(SHORT_DESCRIPTION, bundle.getString("manager.descrNew"));
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			//Crea pannello per la creazione del giocatore
			setBounds(100, 100, 300, 230);
			mainPanel.setVisible(false);
			langPanel.setVisible(true);
		}
	}

	private class Language extends AbstractAction {
		public Language() {
			putValue(NAME, bundle.getString("manager.langName"));
			putValue(SHORT_DESCRIPTION, bundle.getString("manager.descrLang"));
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			//Crea pannello per la creazione del giocatore
			setBounds(100, 100, 300, 230);
			mainPanel.setVisible(false);
			langPanel.setVisible(true);
		}
	}

	//usata nel button new game
	private class Start extends AbstractAction {
		public Start() {
			putValue(NAME, bundle.getString("manager.start"));
			putValue(SHORT_DESCRIPTION, bundle.getString("manager.descrStart"));
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			//INIZIA GIOCO
			JTextField name = (JTextField) newGamePanel.getComponent(1);
			player = name.getText();
			close();
		}
	}

	private class Load extends AbstractAction {
		public Load() {
			putValue(NAME, bundle.getString("manager.load"));
			putValue(SHORT_DESCRIPTION, bundle.getString("manager.descrLoad"));
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			String selectedSave = ((JList<String>) mainPanel.getComponent(3)).getSelectedValue();
			selected = saves.get(selectedSave);
			//seleziona nome giocatore
			player = selectedSave.split(" ")[0];
			close();
		}
	}

	public String getPlayer() {
		return player;
	}

	public GameDescription getSave() {
		return selected;
	}

	private class Back extends AbstractAction {
		public Back() {
			putValue(NAME, bundle.getString("manager.back"));
			putValue(SHORT_DESCRIPTION, bundle.getString("manager.descrBack"));
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			//ripristina pannello principale
			setBounds(100, 100, 450, 300);
			newGamePanel.setVisible(false);
			mainPanel.setVisible(true);
		}
	}

	private void close() {
		this.dispose();
		try {
			db.closeConnection();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private class Delete extends AbstractAction {

		public Delete() {
			putValue(NAME, bundle.getString("manager.delete"));
			putValue(SHORT_DESCRIPTION, bundle.getString("manager.descrDelete"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				String selectedPlayer = ((JList<String>) mainPanel.getComponent(3)).getSelectedValue();
				String[] tokens = selectedPlayer.split("\\s+");
				int index = ((JList<String>) mainPanel.getComponent(3)).getSelectedIndex();
				DefaultListModel<String> m = (DefaultListModel) ((JList<String>) mainPanel.getComponent(3)).getModel();
				m.remove(index);
				db.deleteTuple(tokens[0]);
			}catch (Exception e1) {
				// TODO: handle exception
				System.out.print(e1);
			}

		}
	}

	public void onClose() {
		this.setVisible(false);
		try {
			db.closeConnection();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}