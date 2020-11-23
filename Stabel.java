class Stabel<T> extends Lenkeliste<T> {

    // Legg til et element paa enden av listen
    public void leggPaa(T x) {
        leggTil(stoerrelse(), x);
    }

    // Returner og fjern elementet paa enden av listen
    public T taAv() {
        return fjern(stoerrelse() - 1);
    }

}