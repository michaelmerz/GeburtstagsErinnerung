package functionality;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import datatypes.Entry;

public class MainGUI extends JFrame {

    private JPanel contentPane;
    private JLabel jlActualDate, jlBirthdayReminding;
    private InputGUI inputgui;
    static boolean inputGuiOpened = false;
    private int[] actualSystemDateArr = getActualSystemDate();
    private int sysDay = actualSystemDateArr[0],
	    sysMonth = actualSystemDateArr[1],
	    sysYear = actualSystemDateArr[2];
    private Entry actualSystemDate = new Entry(sysDay, sysMonth, sysYear);
    // BaseFilename, filename und dateSeparator werden von InputGUI ebenfalls
    // verwendet und muessen daher paketsichtbar sein.
    static final String baseFilename = "./.birthdayRemider";
    static final String filename = baseFilename + "/data.txt";
    static final String dateSeparator = ";";
    // Vermutlich ist es nicht sinnvoll, die Variable baseDirectory sowohl in
    // MainGUI, als auch in InputGUI zu verwenden, daher Deklaration in jeder
    // Klasse und private.
    private File baseDirectory = new File(baseFilename);
    private File dataFile = new File(baseFilename + "/data.txt");

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    MainGUI frame = new MainGUI();
		    frame.setVisible(true);
		} catch (Exception e) {
		    // TODO: In Log-Datei schreiben
		    e.printStackTrace();
		}
	    }
	});
    }

    /**
     * Ueberprueft, ob das Verzeichnis ".birthdayReminder" und darin die
     * Textdatei "data.txt" im Home-Verzeichnis des Benutzers (~) vorhanden
     * sind.
     */
    private boolean checkFilesExistence() {
	return baseDirectory.exists() && dataFile.exists();
    }

    /**
     * Legt das Verzeichnis ".birthdayReminder" und darin die Textdatei
     * "data.txt" im Home-Verzeichnis des Benutzers (~) an, ueberschreibt
     * existierende Dateien bzw. Verzeichnisse mit denselben Namen.
     */
    private void createFiles() {
	try {
	    baseDirectory.mkdir();
	    dataFile.createNewFile();
	} catch (Exception e) {
	    JOptionPane.showMessageDialog(null,
		    "Fehler beim Erzeugen der Basisdateien.");
	    // TODO: In Log-Datei schreiben
	    e.printStackTrace();
	}
    }

    /**
     * Gibt das aktuelle Systemdatum als int-Array der Laenge 3 zurück: 0=day,
     * 1=month, 2=year
     */
    private int[] getActualSystemDate() {
	// Den GregorianCalendar automatisch mit der Systemzeit initialisieren:
	GregorianCalendar gc = new GregorianCalendar();
	int year = gc.get(Calendar.YEAR);
	// +1, da die Monate hier bei 0 anfangen (Januar = 0; Dezember = 11)
	int month = gc.get(Calendar.MONTH) + 1;
	int day = gc.get(Calendar.DAY_OF_MONTH);

	int[] result = { day, month, year };
	return result;
    }

    /**
     * Gibt das aktuelle Systemdatum als String in der Form dd.mm.yyyy zurück.
     */
    private String printActualSystemDate() {
	// xx.yy.zzzz = 10 Chars
	StringBuffer sysDate = new StringBuffer(10);

	// Fehlende fuehrende Stellen mit Nullen auffuellen:
	if (sysDay < 10)
	    sysDate.append("Heute ist der 0" + sysDay + ".");
	else
	    sysDate.append("Heute ist der " + sysDay + ".");
	if (sysMonth < 10)
	    sysDate.append("0" + sysMonth + ".");
	else
	    sysDate.append(sysMonth + ".");
	if (sysYear < 1000)
	    sysDate.append("0");
	if (sysYear < 100)
	    sysDate.append("0");
	if (sysYear < 10)
	    sysDate.append("0");
	sysDate.append(sysYear);

	return sysDate.toString();
    }

    /**
     * Oeffnet ein neues InputGUI-Fenster, falls nicht schon eines geoeffnet
     * ist. Ansonsten wird dem geoeffneten InputGUI-Fenster der Fokus gegeben.
     */
    private void openNewInputGui() {
	if (inputGuiOpened) {
	    // TODO: Grund dafuer herausfinden, warum dieser Aufruf der gerade
	    // geoeffneten InputGUI nicht den Fokus gibt und diesen beheben.
	    inputgui.requestFocusInWindow();
	    // Testausgaben:
	    // System.out.println(inputgui.isFocusable());
	    // System.out.println(inputgui.requestFocusInWindow());
	    // System.out.println("InputGUI should have obtained the focus now.");
	} else {
	    inputgui = new InputGUI();
	    // Code verlagert in Klasse InputGUI, da er an dieser Stelle nicht
	    // funktioniert.
	    // inputgui.addWindowListener(new WindowAdapter() {
	    // public void windowClosed(WindowEvent e) {
	    // System.out.println("test"); // Testausgabe
	    // inputgui.dispose();
	    // inputGuiOpened = false;
	    // }
	    // });
	    inputGuiOpened = true;
	    inputgui.main(null);
	}
    }

    /**
     * Durchsucht die Datei "data.txt" nach Eintraegen, deren Geburtsdatum mit
     * dem aktuellen Systemdatum uebereinstimmen und gibt diese als
     * Entry-Objekte in einer ArrayList zurück. Die Rueckabe eines einzelnen
     * Entry-Objektes reicht nicht aus, da so im Fall, dass an einem Tag mehrere
     * Leute Geburtstag haben, nur die Person mit dem lexikographisch ersten
     * ausgegeben werden koennte. Namen
     */
    private ArrayList<Entry> searchFileByDate() {
	File dataFile = new File(filename);
	BufferedReader in;
	String buffer;
	String[] actualEntryArr;
	Entry actualEntry;
	ArrayList<Entry> returnEntries = new ArrayList<Entry>();

	if (!dataFile.exists()) {
	    JOptionPane
		    .showMessageDialog(null,
			    "Offensichtlich wurde die Daten-Datei gelöscht. Bitte Programm neu starten.");
	    return null;
	} else {
	    try {
		in = new BufferedReader(new FileReader(filename));
		// Durchsuche die Datei, so lange es noch neue Zeilen gibt.
		while ((buffer = in.readLine()) != null) {
		    actualEntryArr = buffer.split(dateSeparator);
		    actualEntry = new Entry(actualEntryArr[0],
			    actualEntryArr[1],
			    Integer.parseInt(actualEntryArr[2]),
			    Integer.parseInt(actualEntryArr[3]),
			    Integer.parseInt(actualEntryArr[4]));
		    // Wenn das Geburtsdatum der zum aktuellen Eintrag
		    // gehoerenden Person in Tag und Monat mit dem aktuellen
		    // Systemdatum uebereinstimmt, dann hat diese Person heute
		    // Geburtstag.
		    if (actualEntry.getDay() == actualSystemDate.getDay()
			    && actualEntry.getMonth() == actualSystemDate
				    .getMonth())
			returnEntries.add(actualEntry);

		}
		in.close();
	    } catch (Exception e) {
		JOptionPane.showMessageDialog(null,
			"Die Daten-Datei konnte nicht eingelesen werden.");
		// TODO: In Log-Datei schreiben
		e.printStackTrace();
	    }
	}
	return returnEntries;
    }

    /**
     * Berechnet, wie alt eine durch e abstrahierte Person wird.
     */
    private int getPersonsAge(Entry e) {
	// Alter der durch e abstrahierten Person = Systemjahr - Geburtsjahr
	return sysYear - e.getYear();
    }

    /**
     * Gibt mithilfe von searchFileByDate() und getPersonsAge(Entry e) alle
     * Geburtstagskinder und ihr Alter in der MainGUI aus.
     */
    // TODO: Anderes Fensterelement mit mehr Platz anlegen, falls es mehrere
    // Geburtstagskinder und/oder lange Namen gibt.
    private void checkForBirthdayChilds() {
	ArrayList<Entry> birthdayChilds = searchFileByDate();
	int n = birthdayChilds.size();
	// Keine Person hat Geburtstag
	if (n == 0) {
	    jlBirthdayReminding
		    .setText("Heute hat keine der eingetragenen Personen Geburtstag.");
	    // Eine Person hat Geburtstag
	} else if (n == 1) {
	    Entry person1 = birthdayChilds.get(0);
	    jlBirthdayReminding.setText(person1.getPrename() + " "
		    + person1.getSurname() + " hat heute Geburtstag und wird "
		    + getPersonsAge(person1) + " Jahre alt.");
	    // Mehr als eine Person haben Geburtstag
	} else {
	    jlBirthdayReminding.setText("Heute haben ");
	    for (Entry e : birthdayChilds) {
		// Hinter dem letzten Geburtstagskind soll kein Komma mehr
		// stehen
		if (e == birthdayChilds.get(birthdayChilds.size() - 1))
		    jlBirthdayReminding.setText(jlBirthdayReminding.getText()
			    + e.getPrename() + " " + e.getSurname());
		else
		    jlBirthdayReminding.setText(jlBirthdayReminding.getText()
			    + e.getPrename() + " " + e.getSurname() + ", ");
	    }
	    jlBirthdayReminding.setText(jlBirthdayReminding.getText()
		    + " Geburtstag und werden ");
	    for (Entry e : birthdayChilds) {
		if (e == birthdayChilds.get(birthdayChilds.size() - 1))
		    jlBirthdayReminding.setText(jlBirthdayReminding.getText()
			    + getPersonsAge(e));
		else
		    jlBirthdayReminding.setText(jlBirthdayReminding.getText()
			    + getPersonsAge(e) + ", ");
	    }
	    jlBirthdayReminding.setText(jlBirthdayReminding.getText()
		    + " Jahre alt.");
	}
    }

    /**
     * Create the frame.
     */
    public MainGUI() {
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(100, 100, 580, 419);
	contentPane = new JPanel();
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(contentPane);
	contentPane.setLayout(null);
	setTitle("Birthday Reminder");
	setLocationRelativeTo(null);

	JLabel jlGreeting = new JLabel(
		"Willkommen zum Geburtstags-Erinnerungs-Programm!");
	jlGreeting.setBounds(85, 26, 393, 15);
	contentPane.add(jlGreeting);

	JButton jbCheckAgain = new JButton("Erneut auf Geburtstage überprüfen");
	jbCheckAgain.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		checkForBirthdayChilds();
	    }
	});
	jbCheckAgain.setBounds(134, 237, 286, 25);
	contentPane.add(jbCheckAgain);

	JButton jbAddNewEntry = new JButton("Neuen Eintrag hinzufügen");
	jbAddNewEntry.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		openNewInputGui();
	    }
	});
	jbAddNewEntry.setBounds(168, 292, 224, 25);
	contentPane.add(jbAddNewEntry);

	JButton jbEndProgram = new JButton("Beenden");
	jbEndProgram.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		System.exit(0);
	    }
	});
	jbEndProgram.setBounds(203, 342, 144, 25);
	contentPane.add(jbEndProgram);

	// Gib das aktuelle Systemdatum im der MainGUI aus.
	jlActualDate = new JLabel(printActualSystemDate());
	jlActualDate.setBounds(12, 70, 442, 15);
	contentPane.add(jlActualDate);

	jlBirthdayReminding = new JLabel();
	jlBirthdayReminding.setBounds(12, 121, 472, 15);
	contentPane.add(jlBirthdayReminding);

	// Erzeuge die Dateien ".birthdayReminder" und darin die Textdatei
	// "data.txt" im Home-Verzeichnis des Benutzers (~), falls diese noch
	// nicht vorhanden sind.
	if (!checkFilesExistence())
	    createFiles();
	// Ansonsten gib die gespeicherten Geburtstagskinder aus, falls diese
	// vorhanden sind.
	else
	    checkForBirthdayChilds();
    }
}
