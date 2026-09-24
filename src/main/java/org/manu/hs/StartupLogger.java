package org.manu.hs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Says which version is running, once the API is ready to serve: "what's running?" is then one
 * search in the logs. The version and build time come from Maven's build info
 * (spring-boot-maven-plugin, goal build-info); a run without it, e.g. from the IDE, says so.
 */
@Slf4j
@Component
public class StartupLogger {

    private final Optional<BuildProperties> build;

    public StartupLogger(Optional<BuildProperties> build) {
        this.build = build;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logVersion() {
        build.ifPresentOrElse(
                info -> log.info("Heaped & Stacked API {} (built {}) is ready", info.getVersion(), info.getTime()),
                () -> log.info("Heaped & Stacked API (version unknown: no build info) is ready"));
    }
}
