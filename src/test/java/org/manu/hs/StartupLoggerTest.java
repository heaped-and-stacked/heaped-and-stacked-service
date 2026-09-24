package org.manu.hs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for StartupLogger: what the API says about itself when it is ready.
 */
@ExtendWith(OutputCaptureExtension.class)
class StartupLoggerTest {

    @Test
    @DisplayName("""
            Given a build with a version and a build time
            When the API is ready
            Then one line says which version runs and when it was built, so "what's running?" is one log search
            """)
    void logsVersionAndBuildTime(CapturedOutput output) {
        // Given
        Properties build = new Properties();
        build.setProperty("version", "1.4.0");
        build.setProperty("time", "2026-10-09T10:15:30Z");

        // When
        new StartupLogger(Optional.of(new BuildProperties(build))).logVersion();

        // Then
        assertTrue(output.getAll().contains("Heaped & Stacked API 1.4.0 (built 2026-10-09T10:15:30Z) is ready"),
                output.getAll());
    }

    @Test
    @DisplayName("""
            Given a run without build information, e.g. started from the IDE
            When the API is ready
            Then the line says the version is unknown instead of failing
            """)
    void saysVersionIsUnknownWithoutBuildInfo(CapturedOutput output) {
        // When
        new StartupLogger(Optional.empty()).logVersion();

        // Then
        assertTrue(output.getAll().contains("Heaped & Stacked API (version unknown: no build info) is ready"),
                output.getAll());
    }
}
