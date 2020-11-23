import java.util.Iterator;

class Lenkeliste<T> implements Liste<T> {

    protected Node start;

    public Lenkeliste() {
        start = null;
    }

    // Returnerer antall elementer i listen
    public int stoerrelse() {
        int total = 0;
        Node walk = start;
        while (walk != null) {
            total++;
            walk = walk.neste;
        }
        return total;
    }

    // Legger til et element paa den angitte posisjonen
    public void leggTil(int pos, T x) {
        if (pos < 0 || pos > stoerrelse()) {
            throw new UgyldigListeIndeks(pos);
        }
        if (pos == 0) {
            // Posisjon er 0, legg til foran start
            Node p = start;
            start = new Node(x);
            start.neste = p;
        } else {
            // Finn frem til posisjonen og legg inn noden
            Node walk = start;
            for (int i = 1; i < pos; i++) {
                walk = walk.neste;
            }
            Node p = walk.neste;
            walk.neste = new Node(x);
            walk.neste.neste = p;
        }
    }

    // Legger til et element paa slutten av listen
    public void leggTil(T x) {
        leggTil(stoerrelse(), x);
    }

    // Setter verdien til elementet på den angitte posisjonen
    public void sett(int pos, T x) {
        if (pos < 0 || pos >= stoerrelse()) {
            throw new UgyldigListeIndeks(pos);
        }
        Node walk = start;
        for (int i = 0; i < pos; i++) {
            walk = walk.neste;
        }
        walk.data = x;
    }

    // Returnerer elementet paa den angitte posisjonen
    public T hent(int pos) {
        if (pos < 0 || pos >= stoerrelse()) {
            throw new UgyldigListeIndeks(pos);
        }
        Node walk = start;
        for (int i = 0; i < pos; i++) {
            walk = walk.neste;
        }
        return walk.data;
    }

    // Fjerner elementet paa den angitte posisjonen
    public T fjern(int pos) {
        if (pos < 0 || pos >= stoerrelse()) {
            throw new UgyldigListeIndeks(pos);
        }
        if (pos == 0) {
            // Posisjon er 0, bruk andre funksjonen
            return fjern();
        } else {
            // Finn elementet og flytt neste-pekeren forbi den
            Node walk = start;
            for (int i = 1; i < pos; i++) {
                walk = walk.neste;
            }
            Node p = walk.neste;
            walk.neste = walk.neste.neste;
            return p.data;
        }
    }

    // Sjekk om listen inneholder et element
    public boolean inneholder(T data) {
        Node node = start;
        while (node != null) {
            if (node.data == data) {
                return true;
            }
            node = node.neste;
        }
        return false;
    }

    // Fjerner elementet paa starten av listen
    public T fjern() {
        if (start == null) {
            throw new UgyldigListeIndeks(0);
        }
        Node p = start;
        start = start.neste;
        return p.data;
    }

    // Gir ut et Iterator-objekt for listen
    public Iterator<T> iterator() {
        return new LenkelisteIterator(start);
    }

    @Override
    public String toString() {
        String string = "{";
        Node walk = start;
        while (walk != null) {
            string += walk.data.toString();
            if (walk.neste != null) {
                string += ", ";
            }
            walk = walk.neste;
        }
        string += "}";
        return string;
    }


    protected class Node {

        public T data;
        public Node neste;

        public Node(T data) {
            this.data = data;
            this.neste = null;
        }

    }

    protected class LenkelisteIterator implements Iterator<T> {

        private Node node;

        public LenkelisteIterator(Node node) {
            this.node = node;
        }

        public boolean hasNext() {
            return (node != null);
        }
    
        public T next() {
            T n = node.data;
            node = node.neste;
            return n;
        }
    
    }

}