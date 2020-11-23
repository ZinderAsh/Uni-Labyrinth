import java.io.File;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.stage.FileChooser.ExtensionFilter;

public class Main extends Application {

    private Liste<Rute[]> veier;
    private int veiIndeks = 0;

    public static void main(String[] args) {

        launch();

    }

    @Override
    public void start(Stage stage) {
        try {
            // Velg labyrintfil
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Velg en labyrint.");
            fileChooser.setInitialDirectory(new File("."));
            fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Labyrint", "*.in")
                );
            File labyrintFil = fileChooser.showOpenDialog(stage);

            // Lag labyrint-objekt og ruter fra valgt fil
            Labyrint labyrint = Labyrint.lesFraFil(labyrintFil);

            // Holder på rutenett og rekke med knapper
            FlowPane root = new FlowPane();
            root.setAlignment(Pos.CENTER);
            root.setOrientation(Orientation.VERTICAL);
            
            // Lag rutenett som skal vise ruter
            GridPane pane = new GridPane();
            pane.setAlignment(Pos.CENTER);
            byggLabyrint(pane, labyrint);
            root.getChildren().add(pane);

            // Knapp for å vise forrige mulige utvei
            Button forrigeKnapp = new Button("<");
            forrigeKnapp.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent me) {
                    forrigeVei(pane, labyrint);
                }
            });

            // Knapp for å vise neste mulige utvei
            Button nesteKnapp = new Button(">");
            nesteKnapp.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent me) {
                    nesteVei(pane, labyrint);
                }
            });

            // Knapp for å vise den raskeste veien
            Button raskestKnapp = new Button("Vis raskeste vei");
            raskestKnapp.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent me) {
                    raskesteVei(pane, labyrint);
                }
            });
            raskestKnapp.setAlignment(Pos.CENTER);

            // Tekst som viser antall utveier, og hvilken utvei som blir vist
            Label antallVeier = new Label("Velg en rute");
            antallVeier.setPadding(new Insets(0, 10, 0, 10));
            antallVeier.setId("antall_veier");

            // Panel som holder på knappene og antall utveier
            FlowPane uiPane = new FlowPane(forrigeKnapp, antallVeier, nesteKnapp);
            uiPane.setOrientation(Orientation.HORIZONTAL);
            uiPane.setAlignment(Pos.TOP_CENTER);
            uiPane.setPadding(new Insets(20, 0, 0, 0));

            
            root.getChildren().addAll(uiPane, raskestKnapp);

            // Sett minimal størrelse for å alltid ha plass til labyrinten
            stage.setMinWidth(600);
            stage.setMinHeight(700);

            stage.setTitle("Labyrinter");
            stage.setScene(new Scene(root, 600, 700));

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    System.exit(1);
                }
            });

            stage.show();

        } catch (Exception e) {}
    }

    // Metode som sette opp labyrinten sine ruter for første gang
    public void byggLabyrint(GridPane pane, Labyrint labyrint) {

        int stoerrelse = (int) Math.min(
            Math.ceil(500 / labyrint.hentBredde()),
            Math.ceil(500 / labyrint.hentHoyde()));

        // Fyll rutenett med sorte og hvite ruter
        for (int y = 0; y < labyrint.hentHoyde(); y++) {
            for (int x = 0; x < labyrint.hentBredde(); x++) {
                Rute rute = labyrint.hentRute(x, y);
                
                Rectangle rect = new Rectangle(stoerrelse, stoerrelse,
                    rute.erVegg() ? Color.BLACK : Color.WHITE);
                
                // Sett event for å finne vei når ruten blir trykket på
                rect.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent me) {
                        finnVei(pane, labyrint, rect);
                        rect.setFill(Color.RED);
                    }
                });
                pane.add(rect, x, y);
            }
        }
    }

    // Finner alle utveier, og viser den første på listen hvis den fant en
    public void finnVei(GridPane pane, Labyrint labyrint, Rectangle src) {
        int srcX = GridPane.getColumnIndex(src);
        int srcY = GridPane.getRowIndex(src);
        Liste<String> utveier = labyrint.finnUtveiFra(srcX, srcY);

        veier = new Lenkeliste<Rute[]>();

        // Finn koordinatene for alle mulige veier
        for (String s : utveier) {
            String[] ruter = s.split(" --> ");
            Rute[] vei = new Rute[ruter.length];
            for (int i = 0; i < ruter.length; i++) {
                String s2 = ruter[i];
                int x = Integer.parseInt(s2.substring(1, s2.indexOf(",")));
                int y = Integer.parseInt(s2.substring(s2.indexOf(",") + 2, s2.indexOf(")")));
                vei[i] = labyrint.hentRute(x, y);
            }
            veier.leggTil(vei);
        }

        // Vis veien om det er noen
        veiIndeks = 0;
        if (veier.stoerrelse() > 0) {
            tegnVei(pane, labyrint, veier.hent(0));
        } else {
            tegnLabyrint(pane, labyrint);
            Label antallVeier = (Label) pane.getParent().lookup("#antall_veier");
            antallVeier.setText("Fant ingen veier");
        }
    }

    // Tegner en vei i rødt fra ruten som sist ble trykket på
    public void tegnVei(GridPane pane, Labyrint labyrint, Rute[] vei) {

        Label antallVeier = (Label) pane.getParent().lookup("#antall_veier");
        antallVeier.setText(String.format("%d / %d", veiIndeks + 1, veier.stoerrelse()));

        // Gå gjennom rutenettet og finn ruter som er på veien
        for (Node child : pane.getChildren()) {
            int x = GridPane.getColumnIndex(child);
            int y = GridPane.getRowIndex(child);
            if (!labyrint.hentRute(x, y).erVegg()) {
                boolean paaVei = false;
                for (int i = 0; i < vei.length; i++) {
                    if (vei[i].getX() == x && vei[i].getY() == y) {
                        Rectangle rect = (Rectangle) child;
                        int hue = vei.length <= 30 ? (10 * i) : (340 * i / vei.length); // RAINBOW!
                        rect.setFill(Color.web(String.format("hsl(%d,100%%,100%%)", hue)));
                        paaVei = true;
                        break;
                    }
                }
                if (!paaVei) {
                    Rectangle rect = (Rectangle) child;
                    rect.setFill(labyrint.hentRute(x, y).erVegg() ? Color.BLACK : Color.WHITE);
                }
            }
        }
    }

    // Fyller farge i alle rutene uten å initalisere helt nye rektangler
    public void tegnLabyrint(GridPane pane, Labyrint labyrint) {

        for (Node child : pane.getChildren()) {
            Rute rute = labyrint.hentRute(GridPane.getColumnIndex(child), GridPane.getRowIndex(child));
            Rectangle rect = (Rectangle) child;
            rect.setFill(rute.erVegg() ? Color.BLACK : Color.WHITE);
        }

    }

    public void forrigeVei(GridPane pane, Labyrint labyrint) {

        if (veier != null && veier.stoerrelse() > 0) {
            if (veiIndeks > 0) {
                veiIndeks--;
            }
    
            tegnVei(pane, labyrint, veier.hent(veiIndeks));
        }
    }

    public void nesteVei(GridPane pane, Labyrint labyrint) {

        if (veier != null && veier.stoerrelse() > 0) {
            if (veiIndeks < veier.stoerrelse() - 1) {
                veiIndeks++;
            }
    
            tegnVei(pane, labyrint, veier.hent(veiIndeks));
        }
    }

    // Finn raskeste utvei og vis den
    public void raskesteVei(GridPane pane, Labyrint labyrint) {

        if (veier != null && veier.stoerrelse() > 0) {
            for (int i = 0; i < veier.stoerrelse(); i++) {

                if (veier.hent(i).length < veier.hent(veiIndeks).length) {
                    veiIndeks = i;
                }
    
            }
            tegnVei(pane, labyrint, veier.hent(veiIndeks));
        }
    }

}