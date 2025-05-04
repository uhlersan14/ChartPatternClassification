package ch.zhaw.deeplearningjava.chartpattern;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;

public class Inference {
    private static final Logger logger = LoggerFactory.getLogger(Inference.class);
    private Predictor<Image, Classifications> predictor;
    private static final List<String> DEFAULT_CLASSES = Arrays.asList(
            "SymmetricalTriangle", "RisingWedge", "FallingWedge",
            "DoubleTop", "DoubleBottom", "DescendingTriangle", "AscendingTriangle");

    public Inference() {
        try {
            logger.info("Starte Initialisierung des Modells für Chartmuster-Klassifikation");

            // Modell über Models-Klasse laden
            logger.debug("Lade Modell über Models.getModel()");
            Model model = Models.getModel();
            logger.debug("Modell erfolgreich erstellt.");

            // Modellverzeichnis und -name protokollieren
            Path modelDir = Paths.get("models");
            logger.info("Modellverzeichnis: {}", modelDir.toAbsolutePath());
            logger.info("Modellname: {}", Models.MODEL_NAME);

            // Modell laden
            logger.debug("Lade Modell aus Verzeichnis: {} mit Name: {}", modelDir, Models.MODEL_NAME);
            model.load(modelDir, Models.MODEL_NAME);
            logger.info("Modell erfolgreich geladen");

            // Translator für Bildklassifikation erstellen
            logger.debug("Erstelle ImageClassificationTranslator mit Klassen: {}", DEFAULT_CLASSES);
            Translator<Image, Classifications> translator = ImageClassificationTranslator.builder()
                    .addTransform(new Resize(Models.IMAGE_WIDTH, Models.IMAGE_HEIGHT))
                    .addTransform(new ToTensor())
                    .optApplySoftmax(true)
                    .optSynset(DEFAULT_CLASSES)
                    .build();
            logger.debug("Translator erfolgreich erstellt");

            // Predictor initialisieren
            logger.debug("Initialisiere Predictor");
            predictor = model.newPredictor(translator);
            logger.info("Predictor erfolgreich initialisiert");
        } catch (ModelException e) {
            logger.error("Fehler beim Laden des Modells: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unerwarteter Fehler bei der Initialisierung: {}", e.getMessage(), e);
        }
    }

    public Classifications predict(byte[] image) throws ModelException, TranslateException, IOException {
        // Prüfen, ob Predictor initialisiert wurde
        if (predictor == null) {
            logger.error("Predictor ist nicht initialisiert. Modell konnte nicht geladen werden.");
            throw new ModelException("Predictor ist nicht initialisiert. Überprüfe die Modellinitialisierung.");
        }

        logger.debug("Starte Vorhersage für Bild mit Größe: {} Bytes", image.length);
        InputStream is = new ByteArrayInputStream(image);
        BufferedImage bi = ImageIO.read(is);
        logger.debug("Bild erfolgreich als BufferedImage geladen");

        Image img = ImageFactory.getInstance().fromImage(bi);
        logger.debug("Bild in DJL Image-Format konvertiert");

        Classifications predictResult = predictor.predict(img);
        logger.info("Vorhersage erfolgreich. Ergebnis: {}", predictResult);
        return predictResult;
    }
}