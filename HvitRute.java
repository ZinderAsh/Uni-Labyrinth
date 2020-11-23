class HvitRute extends Rute {

    public HvitRute(Labyrint labyrint, int x, int y) {
        super(labyrint, x, y);
    }

    public char tilTegn() {
        return '.';
    }

    public boolean erVegg() {
        return false;
    }

}