package com.workfusion.academy.task;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.workfusion.academy.model.ExtractionModelResult;
import com.workfusion.academy.module.RepositoryModule;
import com.workfusion.academy.repository.ExtractionModelResultsRepository;
import com.workfusion.odf2.compiler.BotTask;
import com.workfusion.odf2.core.cdi.Injector;
import com.workfusion.odf2.core.cdi.Requires;
import com.workfusion.odf2.core.task.TaskInput;
import com.workfusion.odf2.core.task.generic.GenericTask;
import com.workfusion.odf2.core.task.output.TaskRunnerOutput;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Iterator;

@BotTask
@Requires({RepositoryModule.class})
public class MLOutputTask implements GenericTask {

    private final Logger logger;
    private final TaskInput taskInput;

    private final ExtractionModelResultsRepository extractionModelResultsRepository;

    @Inject
    public MLOutputTask(Injector injector) {
        this.logger = injector.instance(Logger.class);
        this.taskInput = injector.instance(TaskInput.class);
        this.extractionModelResultsRepository = injector.instance(ExtractionModelResultsRepository.class);

    }

    @Override
    public TaskRunnerOutput run() {

        String modelResultJson = taskInput.getRequiredVariable("model_result");
        String extractedValue = org.apache.commons.lang.StringUtils.EMPTY;
        JsonObject jsonObject = JsonParser.parseString(modelResultJson).getAsJsonObject();
        Iterator<JsonElement> iterator = jsonObject.get("tags").getAsJsonArray().iterator();

        while (iterator.hasNext()) {
            JsonObject jsonObj = iterator.next().getAsJsonObject();
            if (jsonObj.get("tag").getAsString().equalsIgnoreCase("invoice_amount")) {
                extractedValue = jsonObj.get("text").getAsString();
                break;
            }
        }

        Document jsoup = Jsoup.parse(taskInput.getRequiredVariable("document"));
        String goldValue = jsoup.select("invoice_amount").attr("data-value");
        ExtractionModelResult extractionModelResult = new ExtractionModelResult();
        extractionModelResult.setExtractedValue(extractedValue);
        extractionModelResult.setGoldValue(goldValue);
        try {
            extractionModelResultsRepository.create(extractionModelResult);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return taskInput.asResult();
    }
}