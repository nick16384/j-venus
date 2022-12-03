package internalCommands;

import java.util.ArrayList;
import java.util.Map;

public class Cipher_General_HighestOccurrenceChar {
	public static String highestOccurrenceChar(ArrayList<String> params, Map<String, String> paramsWithValues) {
		char result = ' ';
		int count = 0;
		
		if (params != null) {
			//Code from https://www.tutorials.de
			///threads/java-methode-die-den-meistgenutzten-buchstaben-eines-strings-herausfindet.409539/
			
			char[] chars = params.get(0).toCharArray(); // String zu char[] konvertieren
			ArrayList<Character> used = new ArrayList<Character>(); // Instanz einer Liste erstellen
			for(char c : chars) // Foreach Schleife mit den Buchstaben des Strings
			    if(!used.contains(c)) // Wenn der Buchstabe noch nicht in der Liste ist ...
			        used.add(c); // Füge den Buchstaben zur Liste hinzu
			
			for(char c : used) // Foreach Schleife zum Durchlaufen der Liste
			{
			    int tempcount = 0; // Integer zum zählen des aktuellen Zeichens
			    for(char c1 : chars) // Buchstaben des Strings durchlaufen und Anzahl von c zählen
			        if(c1 == c)
			            tempcount++;
			    if(tempcount > count) // Wenn die gezählten Zeichen größer als der "Highscore" sind ...
			    {
			        result = c; // Ergebnis überschreiben
			        count = tempcount; // Anzahl überschreiben
			    }
			}
			return String.valueOf(result);
		} else {
			return "reqParamMissing";
		}
	}
}
