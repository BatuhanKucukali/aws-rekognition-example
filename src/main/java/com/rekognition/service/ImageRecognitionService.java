package com.rekognition.service;

import java.util.List;

public interface ImageRecognitionService {
    String detectText(byte[] image);

    List<String> detectCelebrity(byte[] image);
}
