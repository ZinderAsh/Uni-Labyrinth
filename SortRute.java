class SortRute extends Rute {

    public SortRute(Labyrint labyrint, int x, int y) {
        super(labyrint, x, y);
    }

    public char tilTegn() {
        return '#';
    }

    public boolean erVegg() {
        return true;
    }

}