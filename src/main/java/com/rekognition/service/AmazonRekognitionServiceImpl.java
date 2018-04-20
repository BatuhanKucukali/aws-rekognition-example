package com.rekognition.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.rekognition.config.AwsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AmazonRekognitionServiceImpl implements ImageRecognitionService {
    private final AwsConfig config;
    private AmazonRekognition client;

    public AmazonRekognitionServiceImpl(AwsConfig config) {
        this.config = config;
    }

    @PostConstruct
    public void init() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(config.getApiKey(), config.getSecretKey());

        client = AmazonRekognitionClientBuilder
                .standard()
                .withRegion(Regions.EU_WEST_1)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    @Override
    public String detectText(byte[] image) {
        DetectTextRequest request = new DetectTextRequest()
                .withImage(new Image().withBytes(ByteBuffer.wrap(image)));
        try {
            DetectTextResult result = client.detectText(request);
            List<TextDetection> textDetections = result.getTextDetections();

            if (textDetections.isEmpty()) {
                return null;
            }

            StringBuilder content = new StringBuilder();
            for (TextDetection text : textDetections) {
                if ("WORD".equals(text.getType())) {
                    content.append(text.getDetectedText());
                }
                log.info("Detected: " + text.getDetectedText());
                log.info("Confidence: " + text.getConfidence().toString());
                log.info("Id : " + text.getId());
                log.info("Parent Id: " + text.getParentId());
                log.info("Type: " + text.getType());
            }
            return content.toString();
        } catch (AmazonRekognitionException e) {
            log.error("Detect Text Exception :", e);
        }
        return null;
    }

    @Override
    public List<String> detectCelebrity(byte[] image) {
        RecognizeCelebritiesRequest request = new RecognizeCelebritiesRequest()
                .withImage(new Image().withBytes(ByteBuffer.wrap(image)));

        RecognizeCelebritiesResult result = client.recognizeCelebrities(request);

        List<Celebrity> celebs = result.getCelebrityFaces();
        log.info(celebs.size() + " celebrity(s) were recognized");

        List<String> celebrities = new ArrayList<>();

        for (Celebrity celebrity : celebs) {
            log.info("Celebrity recognized: " + celebrity.getName());
            log.info("Celebrity ID: " + celebrity.getId());

            for (String url : celebrity.getUrls()) {
                log.info("Url: " + url);
            }

            celebrities.add(celebrity.getName());
        }

        return celebrities;
    }
}
