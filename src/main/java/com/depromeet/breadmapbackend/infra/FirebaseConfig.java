//package com.depromeet.breadmapbackend.infra;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//
//import javax.annotation.PostConstruct;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//
//@Slf4j
//@Configuration
//public class FirebaseConfig {
//    @Value("${firebase-sdk-path}") // your firebase sdk path
//    private String firebaseSdkPath;
//
//    @PostConstruct
//    public void initialize() {
//        try {
//            FileInputStream serviceAccount =
//                    new FileInputStream("");
//
//            FirebaseOptions options = new FirebaseOptions.Builder()
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
////                    .setProjectId()
//                    .build();
//
//            FirebaseApp.initializeApp(options);
//
//
//        } catch (FileNotFoundException e) {
//            log.error("Firebase ServiceAccountKey FileNotFoundException" + e.getMessage());
//        } catch (IOException e) {
//            log.error("FirebaseOptions IOException" + e.getMessage());
//        }
//
//    }
//}
