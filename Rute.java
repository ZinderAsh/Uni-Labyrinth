abstract class Rute {

    protected Labyrint labyrint;
    protected int x;
    protected int y;
    
    /* Array for naboer for å kunne bruke dem i løkker,
     * og konstanter for deres indekser for å gjøre koden
     * mer leselig da de hentes ut. */
    public static final int OVER = 0;
    public static final int HOEYRE = 1;
    public static final int UNDER = 2;
    public static final int VENSTRE = 3; 
    protected Rute[] naboer;

    public Rute(Labyrint labyrint, int x, int y) {
        this.labyrint = labyrint;
        this.x = x;
        this.y = y;
        naboer = new Rute[4];
    }

    public void settNabo(Rute rute, int indeks) {
        naboer[indeks] = rute;
    }

    // Denne funksjonen er overskrevet innenfor Aapning-klassen
    public void gaa(String vei, Liste<String> utveier, Stabel<Rute> sjekket) {
        // Legg til ruten på veien
        vei = vei + String.format("(%d, %d) --> ", x, y);
        // Legg til ruten for å unngå og gå i sirkel
        sjekket.leggPaa(this);
        // Sjekk hver nabo sine naboer
        for (Rute nabo : naboer) {
            if (nabo != null) {
                // Bare sjekk om naboen ikke er en vegg og ikke er med
                // i den gjeldene veien
                if (!nabo.erVegg() && !sjekket.inneholder(nabo)) {
                    nabo.gaa(vei, utveier, sjekket);
                }
            }
        }
        // Ta av ruten for å tilbakespore til forrige posisjon
        sjekket.taAv();
    }

    // Gi en liste med alle utveier fra ruten
    public Liste<String> finnUtvei() {
        // Ingen veier om ruten er en vegg
        if (erVegg()) {
            return new Lenkeliste<String>();
        }
        // Variabler for å holde på veien, alle utveier,
        // og tidligere besøkte ruter i en stabel for å kunne
        // tilbakespore
        String vei = "";
        Liste<String> utveier = new Lenkeliste<>();
        Stabel<Rute> sjekket = new Stabel<>();
        gaa(vei, utveier, sjekket);
        return utveier;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    abstract public char tilTegn();

    // Metode som returnerer 'true' om ruten er en vegg
    abstract public boolean erVegg();

}