package ch.zhaw.deeplearningjava.chartpattern;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import ai.djl.Model;
import ai.djl.basicmodelzoo.cv.classification.ResNetV1;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Block;

/** A helper class loads and saves model. */
public final class Models {

    // the number of classification labels: 
    // Symmetrical Triangle, Rising Wedge, Falling Wedge, Double Top, 
    // Double Bottom, Descending Triangle, Ascending Triangle
    public static final int NUM_OF_OUTPUT = 7;

    // the height and width for pre-processing of the image
    public static final int IMAGE_HEIGHT = 224;
    public static final int IMAGE_WIDTH = 224;

    // the name of the model
    public static final String MODEL_NAME = "chartpatternclassifier";

    private Models() {}

    public static Model getModel() {
        // create new instance of an empty model
        Model model = Model.newInstance(MODEL_NAME);

        // Block is a composable unit that forms a neural network; combine them like Lego blocks
        // to form a complex network
        Block resNet50 =
                ResNetV1.builder() // construct the network
                        .setImageShape(new Shape(3, IMAGE_HEIGHT, IMAGE_WIDTH))
                        .setNumLayers(50)
                        .setOutSize(NUM_OF_OUTPUT)
                        .build();

        // set the neural network to the model
        model.setBlock(resNet50);
        return model;
    }

    public static void saveSynset(Path modelDir, List<String> synset) throws IOException {
        Path synsetFile = modelDir.resolve("synset.txt");
        try (Writer writer = Files.newBufferedWriter(synsetFile)) {
            writer.write(String.join("\n", synset));
        }
    }
}