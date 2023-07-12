package com.workfusion.academy.task;

import com.workfusion.odf2.compiler.BotTask;
import com.workfusion.odf2.core.cdi.Injector;
import com.workfusion.odf2.core.cdi.Requires;
import com.workfusion.odf2.core.settings.Configuration;
import com.workfusion.odf2.core.task.TaskInput;
import com.workfusion.odf2.core.task.generic.GenericTask;
import com.workfusion.odf2.core.task.output.TaskRunnerOutput;
import com.workfusion.odf2.service.ControlTowerServicesModule;
import org.slf4j.Logger;

import javax.inject.Inject;

@BotTask
@Requires(ControlTowerServicesModule.class)
public class MLInputTask implements GenericTask {

    private final Logger logger;
    private final TaskInput taskInput;
    private final Configuration configuration;

    private static final String MODEL_ID = "trained.model.id";

    @Inject
    public MLInputTask(Injector injector) {
        this.logger = injector.instance(Logger.class);
        this.taskInput = injector.instance(TaskInput.class);
        this.configuration = injector.instance(Configuration.class);
    }

    @Override
    public TaskRunnerOutput run() {

        String document = taskInput.getRequiredVariable("message");
        String modelId = configuration.getRequiredProperty(MODEL_ID);

        return taskInput.asResult()
                .withColumn("model_id", modelId)
                .withColumn("document", document);
    }

}