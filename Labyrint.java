import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class Labyrint {

    protected Rute[][] ruter;
    protected int rader;
    protected int kolonner;

    private Labyrint(int rader, int kolonner, Rute[][] ruter) {
        this.rader = rader;
        this.kolonner = kolonner;
        this.ruter = ruter;
    }


    public static Labyrint lesFraFil(File file) throws FileNotFoundException {
        
        Scanner scanner = new Scanner(file);

        // Les første linje for antall rader og kolonner
        String line = scanner.nextLine();
        String[] data = line.split(" ");
        int rader = Integer.parseInt(data[0]);
        int kolonner = Integer.parseInt(data[1]);

        // Les resten av linjene for rutene
        Rute[][] ruter = new Rute[rader][kolonner];
        Labyrint labyrint = new Labyrint(rader, kolonner, ruter);

        for (int y = 0; y < rader; y++) {
            line = scanner.nextLine();
            for (int x = 0; x < kolonner; x++) {
                if (line.charAt(x) == '.') {
                    // Ruten er hvit
                    if (x == 0 || x == (kolonner - 1) ||
                        y == 0 || y == (rader - 1)) {
                        // Ruten er en åpning
                        Aapning rute = new Aapning(labyrint, x, y);
                        ruter[y][x] = rute;
                    } else {
                        // Ruten er ikke en åpning
                        HvitRute rute = new HvitRute(labyrint, x, y);
                        ruter[y][x] = rute;
                    }
                } else {
                    // Ruten er sort
                    SortRute rute = new SortRute(labyrint, x, y);
                    ruter[y][x] = rute;
                }
            }
        }

        settNaboer(labyrint);

        scanner.close();

        return labyrint;
    }

    public int hentBredde() {
        return kolonner;
    }

    public int hentHoyde() {
        return rader;
    }

    public Rute hentRute(int x, int y) {
        return ruter[y][x];
    }

    private static void settNaboer(Labyrint labyrint) {

        Rute[][] ruter = labyrint.ruter;

        // Gå gjennom alle rutene og sett naboer på hver av dem
        for (int y = 0; y < labyrint.rader; y++) {
            for (int x = 0; x < labyrint.kolonner; x++) {
                // Unngå å legge til naboer som er utenfor
                // indeksene til arrayen
                if (y > 0) { // Legg til ruten over
                    ruter[y][x].settNabo(ruter[y-1][x], Rute.OVER);
                }
                if (x > 0) { // Ruten til venstre
                    ruter[y][x].settNabo(ruter[y][x-1], Rute.VENSTRE);
                }
                if (y < labyrint.rader - 1) { //  Ruten under
                    ruter[y][x].settNabo(ruter[y+1][x], Rute.UNDER);
                }
                if (x < labyrint.kolonner - 1) { // Ruten til høyre
                    ruter[y][x].settNabo(ruter[y][x+1], Rute.HOEYRE);
                }
            }
        }

    }

    static boolean[][] losningStringTilTabell(String losningString, int bredde, int hoyde) {
        boolean[][] losning = new boolean[hoyde][bredde];
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\(([0-9]+),([0-9]+)\\)");
        java.util.regex.Matcher m = p.matcher(losningString.replaceAll("\\s",""));
        while (m.find()) {
            int x = Integer.parseInt(m.group(1));
            int y = Integer.parseInt(m.group(2));
            losning[y][x] = true;
        }
        return losning;
    }

    // Returnerer en liste av alle mulige utveier fra den gitte posisjonen
    public Liste<String> finnUtveiFra(int x, int y) {
        Rute rute = ruter[y][x];
        Liste<String> liste = rute.finnUtvei();
        return liste;
    }

    @Override
    public String toString() {
        String string = "";
        for (int y = 0; y < rader; y++) {
            for (int x = 0; x < kolonner; x++) {
                string += ruter[y][x].tilTegn();
            }
            string += "\n";
        }
        return string;
    }

}