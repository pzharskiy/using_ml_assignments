package com.workfusion.academy.task;

import java.time.Duration;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.workfusion.spoke.Await;
import com.workfusion.spoke.ControlTower;
import com.workfusion.spoke.bp.BusinessProcessSuccessfulResult;
import com.workfusion.spoke.bundle.AssetBundle;
import com.workfusion.spoke.configuration.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class ExampleBusinessProcessIT {

    private String projectVersion;
    private String bundlePath;

    @BeforeEach
    void setUp() {
        ProjectProperties projectProperties = new ProjectProperties();
        projectVersion = projectProperties.getProjectVersion();
        bundlePath = projectProperties.getDefaultBundlePath();
    }

    @Test
    @DisplayName("should run Example BP")
    void shouldRunExampleBp() {
        // given
        ControlTower controlTower = ControlTower.createConfigured(Configuration.fromFileInUserHome("spoke-it.properties")
                .orElse(Configuration.fromFileSpecifiedInSystemProperty()));

        // when
        BusinessProcessSuccessfulResult result = controlTower.importAssetBundle(AssetBundle.fromFile(bundlePath))
                .getBusinessProcessByZipName(String.format("Example_BP_v_%s.zip", projectVersion))
                .run()
                .waitFor(Await.atMost(Duration.ofMinutes(3)).checkingEvery(Duration.ofSeconds(10)))
                .untilFinished()
                .expectSuccessful();

        // then
        Collection<String> actualData = result.getOutputDataAsCSV()
                .asTable()
                .column("example_bot_task_output")
                .values();

        assertThat(actualData).hasSize(1).containsOnly("completed_successfully");
    }

}
