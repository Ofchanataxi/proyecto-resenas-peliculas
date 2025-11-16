package edu.espe.proyectoresenasbackend.web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/resenas")
public class VersionController {
    // Estos valores se inyectan desde application.yml
    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String version;

    @Value("${app.build-number}")
    private String buildNumber;

    @Value("${app.commit-hash}")
    private String commitHash;

    @GetMapping("/version")
    public Map<String, String> getVersion() {
        Map<String, String> versionInfo = new HashMap<>();
        versionInfo.put("appName", appName);
        versionInfo.put("version", version);
        versionInfo.put("buildNumber", buildNumber);
        versionInfo.put("commitHash", commitHash);
        versionInfo.put("timestamp", Instant.now().toString());
        return versionInfo;
    }
}
