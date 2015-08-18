package datatypes;

import javax.swing.JOptionPane;

public class Entry {
    private String prename, surname;
    private int day, month, year;

    public static final int minDay = 1, maxDay = 31, minMonth = 1,
	    maxMonth = 12, minYear = 0, maxYear = 9999;
    public static final String blank = " ", dash = "-", errorString = "error";

    public Entry(String prename, String surname) {
	setPrename(prename);
	setSurname(surname);
    }

    /**
     * Ohne year
     */
    public Entry(String prename, String surname, int day, int month) {
	this(prename, surname);
	setDay(day);
	setMonth(month);
    }

    public Entry(String prename, String surname, int day, int month, int year) {
	this(prename, surname, day, month);
	setYear(year);
    }

    /**
     * Ohne year
     */
    public Entry(int day, int month) {
	setDay(day);
	setMonth(month);
    }

    public Entry(int day, int month, int year) {
	// Würde eine NullPointerException auslösen:
	// this(null, null, day, month, year);
	this(day, month);
	setYear(year);
    }

    public int getDay() {
	return day;
    }

    public void setDay(int day) {
	if (day >= minDay && day <= maxDay)
	    this.day = day;
	else {
	    this.day = -1;
	    JOptionPane.showMessageDialog(null, "Ungültige Tages-Eingabe!");
	}
    }

    public int getMonth() {
	return month;
    }

    public void setMonth(int month) {
	if (month >= minMonth && month <= maxMonth)
	    this.month = month;
	else {
	    this.month = -1;
	    JOptionPane.showMessageDialog(null, "Ungültige Monats-Eingabe!");
	}
    }

    public int getYear() {
	return year;
    }

    public void setYear(int year) {
	if (year >= minYear && year <= maxYear)
	    this.year = year;
	else {
	    this.year = -1;
	    JOptionPane.showMessageDialog(null, "Ungültige Jahres-Eingabe");
	}
    }

    public String getPrename() {
	return prename;
    }

    public void setPrename(String prename) {
	if (prename.length() <= 30 && !prename.startsWith(blank)
		&& !prename.startsWith(dash) && !prename.endsWith(blank)
		&& !prename.endsWith(dash))
	    this.prename = prename;
	else {
	    this.prename = errorString;
	    JOptionPane.showMessageDialog(null, "Ungültiger Vorname!");
	}

    }

    public String getSurname() {
	return surname;
    }

    public void setSurname(String surname) {
	if (surname.length() <= 30 && !surname.startsWith(blank)
		&& !surname.startsWith(dash) && !surname.endsWith(blank)
		&& !surname.endsWith(dash))
	    this.surname = surname;
	else {
	    this.surname = errorString;
	    JOptionPane.showMessageDialog(null, "Ungültiger Nachname!");
	}
    }

    public String getDate() {
	return Integer.toString(day) + "." + Integer.toString(month) + "."
		+ Integer.toString(year);
    }
}
