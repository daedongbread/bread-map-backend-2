package com.depromeet.breadmapbackend.global.infra;

import com.depromeet.breadmapbackend.global.infra.properties.CustomFirebaseProperties;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FirebaseConfig {
    private final CustomFirebaseProperties customFirebaseProperties;

    @PostConstruct
    public void initialize() {
        String firebaseCredentials = customFirebaseProperties.getCredentials();

        try (InputStream serviceAccount = new ByteArrayInputStream(firebaseCredentials.getBytes());) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials
                            .fromStream(serviceAccount)
                            .createScoped(List.of(customFirebaseProperties.getScope())))
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
