package com.rekognition

import com.rekognition.service.ImageRecognitionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class ImageRecognitionServiceTest extends Specification {
    @Autowired
    ImageRecognitionService imageRecognitionService

    def "detect text expected null"() {
        given:
        def image = new FileInputStream("src/test/resources/static/amazon-go-shelf.png")
        when:
        def text = imageRecognitionService.detectText(image.getBytes())
        then:
        text == null
    }

    def "detect text expected text"() {
        given:
        def image = new FileInputStream("src/test/resources/static/amazon-go-logo.jpg")
        when:
        def text = imageRecognitionService.detectText(image.getBytes())
        then:
        text.contains("amazongo")
    }
}
