package com.smart_receipt.config;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

@Configuration
public class TesseractConfig {

    @Value("${tesseract.datapath}")
    private String datapath;

    @Bean
    public ITesseract tesseract() {
        Tesseract tesseract = new Tesseract();

        File path = new File(datapath);
        if (!path.exists()) {
            try {
                path = new ClassPathResource("tessdata").getFile();
            } catch (IOException e) {
                throw new IllegalStateException("Не удалось найти папку tessdata", e);
            }
        }

        tesseract.setDatapath(path.getAbsolutePath());
        tesseract.setLanguage("rus+eng");
        return tesseract;
    }
}

