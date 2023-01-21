package com.depromeet.breadmapbackend.infra;

import com.depromeet.breadmapbackend.infra.properties.CustomFirebaseProperties;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FirebaseConfig {
    private final CustomFirebaseProperties customFirebaseProperties;

    @PostConstruct
    public void initialize() {
        ClassPathResource resource = new ClassPathResource(customFirebaseProperties.getPath());

        try (InputStream serviceAccount = resource.getInputStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials
                            .fromStream(serviceAccount)
                            .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform")))
                    .setProjectId(customFirebaseProperties.getProjectId())
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("FirebaseApp initialization complete");
            }
        } catch (FileNotFoundException e) {
            log.error("Firebase ServiceAccountKey FileNotFoundException : " + e.getMessage());
        } catch (IOException e) {
            log.error("FirebaseOptions IOException : " + e.getMessage());
        }
    }
}
