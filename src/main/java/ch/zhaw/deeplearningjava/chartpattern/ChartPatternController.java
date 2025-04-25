package ch.zhaw.deeplearningjava.chartpattern;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class ChartPatternController {

    private Inference inference = new Inference();
    private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/ping")
    public String ping() {
        return "Chart Pattern Classification app is up and running!";
    }

    @PostMapping(path = "/analyze")
    public String predict(@RequestParam("image") MultipartFile image) {
        try {
            System.out.println("Analyzing chart pattern: " + image.getOriginalFilename());
            return inference.predict(image.getBytes()).toJson();
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error analyzing image: " + e.getMessage());
            try {
                return objectMapper.writeValueAsString(error);
            } catch (Exception ex) {
                return "{\"error\": \"Failed to analyze image\"}";
            }
        }
    }
}