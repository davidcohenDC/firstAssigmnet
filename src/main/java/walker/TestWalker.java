package walker;

import java.io.File;
import java.io.IOException;

public class TestWalker {


    /**
     Realizzare un programma concorrente che, data una directory D presente sul file system locale contenente un
     insieme di sorgenti in Java (considerando anche qualsiasi sottodirectory, ricorsivamente), provveda a determinare
     e visualizzare in standard output
     ○	gli N sorgenti con il numero maggiore di linee di codice
     ○	La distribuzione complessiva relativa a quanti sorgenti hanno un numero di linee di codice che ricade
     in un certo intervallo, considerando un certo numero d'intervalli NI e un numero massimo MAXL di linee di codice
     per delimitare l'estremo sinistro dell'ultimo intervallo.
     N, D, NI e MAXL si presuppone siano parametri del programma, passati da linea di comando.
     ■	Esempio: se NI = 5 e MAXL è 1000, allora il primo intervallo è [0,249], il secondo è  [250,499], il terzo è
     [500,749], il quarto è [750,999], l'ultimo è [1000,infinito]. La distribuzione determina quanti sorgenti ci sono
     per ogni intervallo  Ho un quadro Java dei sorgenti che ho (considerati tutti i sorgenti).
     */
     public static void main(String[] args) {
        if (args.length != Constants.Arguments.ARGUMENTS_SIZE) {
            System.out.println("Usage: <directory> <number of intervals> <max length of interval>");
            System.exit(1);
        }

        String directory = args[Constants.Arguments.DIRECTORY];
        int maxFiles = Integer.parseInt(args[Constants.Arguments.N_FILES]);
        int numIntervals = Integer.parseInt(args[Constants.Arguments.NUMBER_OF_INTERVALS]);
        int maxLength = Integer.parseInt(args[Constants.Arguments.MAX_LINES]);

        if (numIntervals <= 0 || maxLength <= 0) {
            System.out.println("The number of intervals and the max length of interval must be greater than 0");
            System.exit(1);
        }

        File dir = new File(directory);
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("The directory " + directory + " does not exist");
            System.exit(1);
        }

        Walker walker = new SimpleWalker(dir.toPath(), maxFiles, numIntervals, maxLength);
         try {
                walker.walk();
         } catch (IOException e) {
             throw new RuntimeException(e);
         }


     }
}
