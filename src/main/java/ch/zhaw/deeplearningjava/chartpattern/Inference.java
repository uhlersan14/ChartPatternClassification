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
                    // Use MXNet engine instead of PyTorch
                    Model model = Model.newInstance(Models.MODEL_NAME);  // Remove the engine parameter
                    Path modelDir = Paths.get("models");
                    
                    System.out.println("Looking for model in directory: " + modelDir.toAbsolutePath());
                    
                    // Load the model - MXNet doesn't need the file extension in the load method
                    model.load(modelDir, Models.MODEL_NAME);
                    System.out.println("Model loaded successfully");
            
                    // Create the translator
                    Translator<Image, Classifications> translator = ImageClassificationTranslator.builder()
                            .addTransform(new Resize(Models.IMAGE_WIDTH, Models.IMAGE_HEIGHT))
                            .addTransform(new ToTensor())
                            .optApplySoftmax(true)
                            .optSynset(DEFAULT_CLASSES)
                            .build();
                    
                    predictor = model.newPredictor(translator);
                    System.out.println("Predictor initialized successfully!");
            
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