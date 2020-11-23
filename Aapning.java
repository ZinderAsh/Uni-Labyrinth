class Aapning extends HvitRute {

    public Aapning(Labyrint labyrint, int x, int y) {
        super(labyrint, x, y);
    }

    public char tilTegn() {
        return '.';
    }

    @Override
    public void gaa(String vei, Liste<String> utveier, Stabel<Rute> sjekket) {
        // Legg til ruten på veien
        vei = vei + String.format("(%d, %d)", x, y);
        // Ruten er en åpning, legg til på utvei listen
        utveier.leggTil(vei + "\n");
    }

}