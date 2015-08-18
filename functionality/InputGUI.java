package functionality;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import datatypes.Entry;

public class InputGUI extends JFrame {

    private JPanel jpContentPane;
    private JTextField jtDayInput, jtMonthInput, jtYearInput, jtPrenameInput,
	    jtSurnameInput;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    InputGUI frame = new InputGUI();
		    frame.setVisible(true);
		} catch (Exception e) {
		    // TODO: In Log-Datei schreiben
		    e.printStackTrace();
		}
	    }
	});
    }

    /**
     * Wird von den Key-Listenern aller im Konstruktor (this) verwendeten
     * Textfelder verwendet, um durch Eingabe der Enter-Taste einen neuen
     * Eintrag hinzuzufuegen.
     */
    private void textFieldsEnterPressed(KeyEvent e) {
	if (e.getKeyCode() == KeyEvent.VK_ENTER)
	    addEntry();
    }

    /**
     * Schreibt einen neuen Eintrag in "data.txt", falls dieser nicht in Vor-
     * und Nachname mit einem bereits enthaltenen Eintrag uebereinstimmt. Die zu
     * schreibenden Daten werden direkt aus den JTextFields jtPrenameInput,
     * jtSurnameInput, jtDayInput, jtMonthInput und jtYearInput ausgelesen.
     */
    private void addEntry() {
	File dataFile = new File(MainGUI.filename);
	String prename, surname;
	int day, month, year;
	Entry entry;

	try {
	    prename = jtPrenameInput.getText().trim();
	    surname = jtSurnameInput.getText().trim();
	    day = Integer.parseInt(jtDayInput.getText().trim());
	    month = Integer.parseInt(jtMonthInput.getText().trim());
	    year = Integer.parseInt(jtYearInput.getText().trim());

	    // Ueberpruefe, ob bereits ein Eintrag mit demselben Vor- und
	    // Nachnamen vorhanden ist.
	    if (searchFileByName(prename, surname)) {
		JOptionPane
			.showMessageDialog(
				null,
				"Es ist bereits ein Eintrag mit diesem Vor- und Nachnamen vorhanden. Bitte anderen Namen angeben.");
	    } else {
		entry = new Entry(prename, surname, day, month, year);

		// Ueberpruefe, ob die Entry-Klasse keine Fehlermeldung geworfen
		// hat:
		if (entry.getDay() != -1 && entry.getMonth() != -1
			&& entry.getYear() != -1
			&& entry.getPrename() != Entry.errorString
			&& entry.getSurname() != Entry.errorString) {
		    if (JOptionPane.showConfirmDialog(null,
			    "Soll der Eintrag gespeichert werden?") == 0) {
			// Speichere den neuen Eintrag in der Datei "data.txt"
			if (!dataFile.exists()) {
			    JOptionPane
				    .showMessageDialog(null,
					    "Offensichtlich wurde die Daten-Datei gelöscht. Bitte Programm neu starten.");
			} else {
			    BufferedWriter out;
			    try {
				out = new BufferedWriter(new FileWriter(
					MainGUI.filename, true));

				out.write(entry.getPrename());
				out.write(MainGUI.dateSeparator);
				out.write(entry.getSurname());
				out.write(MainGUI.dateSeparator);
				out.write(Integer.toString(entry.getDay()));
				out.write(MainGUI.dateSeparator);
				out.write(Integer.toString(entry.getMonth()));
				out.write(MainGUI.dateSeparator);
				out.write(Integer.toString(entry.getYear()));
				out.newLine();
				out.close();

				JOptionPane.showMessageDialog(null,
					"Der Eintrag wurde gespeichert!");
			    } catch (Exception e) {
				JOptionPane
					.showMessageDialog(null,
						"Eintrag konnte nicht gespeichert werden!");
				e.printStackTrace();
			    }
			}
		    } else {
			JOptionPane.showMessageDialog(null,
				"Eintrag konnte nicht gespeichert werden!");
		    }
		}
	    }
	} catch (NumberFormatException e) {
	    JOptionPane.showMessageDialog(null, "Ungültige/fehlende Eingabe!");
	    // TODO: In Log-Datei schreiben
	    e.printStackTrace();
	}

    }

    /**
     * Durchsucht die Datei "data.txt" nach einem Eintrag mit @param prename und @param
     * surname. @return true, falls @param prename und @param surname im selben
     * Eintrag in der Datei vorkommen, @return false sonst.
     */
    private boolean searchFileByName(String prename, String surname) {
	BufferedReader in;
	String buffer;
	String[] nameEntryArr;
	Entry nameEntry;
	try {
	    in = new BufferedReader(new FileReader(MainGUI.filename));
	    while ((buffer = in.readLine()) != null) {
		nameEntryArr = buffer.split(MainGUI.dateSeparator);
		nameEntry = new Entry(nameEntryArr[0], nameEntryArr[1]);
		if (nameEntry.getPrename().equals(prename)
			&& nameEntry.getSurname().equals(surname)) {
		    in.close();
		    return true;
		}
	    }
	    in.close();
	} catch (Exception e) {
	    // TODO: Sinnvolle, individuelle Fehlermeldung ausgeben
	    JOptionPane.showMessageDialog(null,
		    "Die Daten-Datei konnte nicht durchsucht werden.");
	    // TODO: In Log-Datei schreiben
	    e.printStackTrace();
	}
	return false;
    }

    /**
     * Create the frame.
     */
    public InputGUI() {
	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	setBounds(100, 100, 550, 280);
	jpContentPane = new JPanel();
	jpContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(jpContentPane);
	jpContentPane.setLayout(null);
	setTitle("Neuen Eintrag hinzufügen");
	setLocationRelativeTo(null); // Setze das Fenster (den Frame) in die
				     // Mitte des Bildschirmes

	// Dieser WindowListener verhindert, dass von MainGUI aus mehr als ein
	// InputGUI-Fenster geoeffnet werden kann.
	addWindowListener(new WindowAdapter() {
	    public void windowClosed(WindowEvent e) {
		dispose();
		// Wieso ist dies ein statischer Verweis, sodass inputGuiOpened
		// in MainGUI als statisch deklariert werden muss?
		MainGUI.inputGuiOpened = false;
	    }
	});

	{
	    jtPrenameInput = new JTextField("Vorname");
	    jtPrenameInput.addFocusListener(new FocusAdapter() {
		@Override
		public void focusGained(FocusEvent e) {
		    jtPrenameInput.selectAll();
		}
	    });
	    jtPrenameInput.addKeyListener(new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
		    textFieldsEnterPressed(e);
		}
	    });
	    jtPrenameInput.setBounds(12, 49, 114, 19);
	    jpContentPane.add(jtPrenameInput);
	    jtPrenameInput.setColumns(10);

	    jtSurnameInput = new JTextField("Nachname");
	    jtSurnameInput.addFocusListener(new FocusAdapter() {
		@Override
		public void focusGained(FocusEvent e) {
		    jtSurnameInput.selectAll();
		}
	    });
	    jtSurnameInput.addKeyListener(new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
		    textFieldsEnterPressed(e);
		}
	    });
	    jtSurnameInput.setBounds(155, 49, 114, 19);
	    jpContentPane.add(jtSurnameInput);
	    jtSurnameInput.setColumns(10);
	}

	{
	    jtDayInput = new JTextField("Tag");
	    jtDayInput.addFocusListener(new FocusAdapter() {
		@Override
		public void focusGained(FocusEvent e) {
		    jtDayInput.selectAll();
		}
	    });
	    jtDayInput.addKeyListener(new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
		    textFieldsEnterPressed(e);
		}
	    });
	    jtDayInput.setBounds(12, 129, 59, 19);
	    jpContentPane.add(jtDayInput);
	    jtDayInput.setColumns(10);

	    jtMonthInput = new JTextField("Monat");
	    jtMonthInput.addFocusListener(new FocusAdapter() {
		@Override
		public void focusGained(FocusEvent e) {
		    jtMonthInput.selectAll();
		}
	    });
	    jtMonthInput.addKeyListener(new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
		    textFieldsEnterPressed(e);
		}
	    });
	    jtMonthInput.setBounds(83, 129, 59, 19);
	    jpContentPane.add(jtMonthInput);
	    jtMonthInput.setColumns(10);

	    jtYearInput = new JTextField("Jahr");
	    jtYearInput.addFocusListener(new FocusAdapter() {
		@Override
		public void focusGained(FocusEvent e) {
		    jtYearInput.selectAll();
		}
	    });
	    jtYearInput.addKeyListener(new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
		    textFieldsEnterPressed(e);
		}
	    });
	    jtYearInput.setBounds(153, 129, 59, 19);
	    jpContentPane.add(jtYearInput);
	    jtYearInput.setColumns(10);
	}

	JLabel jlNameHint = new JLabel(
		"Vor- und Nachname der hinzuzufügenden Person eingeben:");
	jlNameHint.setBounds(12, 22, 482, 15);
	jpContentPane.add(jlNameHint);

	JLabel jlDateHint = new JLabel(
		"Geburtsdatum der hinzuzufügenden Person eingeben:");
	jlDateHint.setBounds(12, 102, 397, 15);
	jpContentPane.add(jlDateHint);

	JButton jbAddEntry = new JButton("Eintrag hinzufügen");
	jbAddEntry.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		addEntry();
	    }
	});
	jbAddEntry.setBounds(12, 186, 174, 25);
	jpContentPane.add(jbAddEntry);
    }
}
