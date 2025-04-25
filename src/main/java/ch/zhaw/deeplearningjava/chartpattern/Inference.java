package ch.zhaw.deeplearningjava.chartpattern;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import ai.djl.Device;
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

    private Predictor<Image, Classifications> predictor;
    private static final List<String> DEFAULT_CLASSES = Arrays.asList(
            "SymmetricalTriangle", "RisingWedge", "FallingWedge", 
            "DoubleTop", "DoubleBottom", "DescendingTriangle", "AscendingTriangle");

    public Inference() {
        try {
            // Use PyTorch engine
            Model model = Model.newInstance(Models.MODEL_NAME, Device.cpu(), "pytorch");
            Path modelDir = Paths.get("models");
            Path modelPath = modelDir.resolve(Models.MODEL_NAME + ".pt");
            
            // Check if model exists
            if (Files.exists(modelPath)) {
                // Load existing trained model
                System.out.println("Loading model from: " + modelPath);
                model.load(modelDir, Models.MODEL_NAME);
            } else {
                // Use a new model with default parameters
                System.out.println("Model file not found at: " + modelPath);
                System.out.println("Creating a new model. Note: This model won't be accurate without training!");
                model.setBlock(Models.getModel().getBlock());
            }

            // Create the translator
            Translator<Image, Classifications> translator = ImageClassificationTranslator.builder()
                    .addTransform(new Resize(Models.IMAGE_WIDTH, Models.IMAGE_HEIGHT))
                    .addTransform(new ToTensor())
                    .optApplySoftmax(true)
                    .optSynset(DEFAULT_CLASSES)
                    .build();
            
            predictor = model.newPredictor(translator);
            System.out.println("Model loaded successfully!");

        } catch (Exception e) {
            System.err.println("Error initializing model: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Classifications predict(byte[] image) throws ModelException, TranslateException, IOException {
        // Check if predictor was initialized properly
        if (predictor == null) {
            throw new ModelException("Model predictor is not initialized. Have you trained the model?");
        }
        
        InputStream is = new ByteArrayInputStream(image);
        BufferedImage bi = ImageIO.read(is);
        Image img = ImageFactory.getInstance().fromImage(bi);

        Classifications predictResult = this.predictor.predict(img);
        return predictResult;
    }
}