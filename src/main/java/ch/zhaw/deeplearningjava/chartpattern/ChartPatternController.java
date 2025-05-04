package ch.zhaw.deeplearningjava.chartpattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import ai.djl.modality.Classifications;

@RestController
public class ChartPatternController {

    private final Inference inference = new Inference();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/ping")
    public String ping() {
        return "Chart Pattern Classification app is up and running!";
    }

    @PostMapping(path = "/analyze")
    public Object predict(@RequestParam("image") MultipartFile image) {
        try {
            System.out.println("Analyzing chart pattern: " + image.getOriginalFilename());
            Classifications classifications = inference.predict(image.getBytes());
            List<Map<String, Object>> results = new ArrayList<>();
            for (Classifications.Classification c : classifications.items()) {
                Map<String, Object> entry = new HashMap<>();
                entry.put("className", c.getClassName());
                entry.put("probability", c.getProbability());
                results.add(entry);
            }
            return results;

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error analyzing image: " + e.getMessage());
            return error;
        }
    }
}