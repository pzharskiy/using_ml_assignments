package com.workfusion.academy.task;

import com.workfusion.academy.model.ExtractionModelResult;
import com.workfusion.academy.module.RepositoryModule;
import com.workfusion.academy.repository.ExtractionModelResultsRepository;
import com.workfusion.odf2.compiler.BotTask;
import com.workfusion.odf2.core.cdi.Injector;
import com.workfusion.odf2.core.cdi.Requires;
import com.workfusion.odf2.core.task.TaskInput;
import com.workfusion.odf2.core.task.generic.GenericTask;
import com.workfusion.odf2.core.task.output.TaskRunnerOutput;
import com.workfusion.odf2.service.ControlTowerServicesModule;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@BotTask
@Requires({ControlTowerServicesModule.class, RepositoryModule.class})
public class CalculateMLStatisticsTask implements GenericTask {

    private final ExtractionModelResultsRepository extractionModelResultsRepository;
    private final Logger logger;
    private final TaskInput taskInput;

    @Inject
    public CalculateMLStatisticsTask(Injector injector) {
        this.extractionModelResultsRepository = injector.instance(ExtractionModelResultsRepository.class);
        this.logger = injector.instance(Logger.class);
        this.taskInput = injector.instance(TaskInput.class);
    }

    @Override
    public TaskRunnerOutput run() {

        List<ExtractionModelResult> extractionModelResults;
        AtomicInteger tp = new AtomicInteger();
        AtomicInteger tn = new AtomicInteger();
        AtomicInteger fp = new AtomicInteger();
        AtomicInteger fn = new AtomicInteger();
        double precision = 0.0;
        double recall = 0.0;
        double accuracy = 0.0;
        double f1 = 0.0;

        try {
            extractionModelResults = extractionModelResultsRepository.getAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        extractionModelResults.forEach(extractionModelResult -> {

            if (extractionModelResult.getGoldValue().isEmpty() || extractionModelResult.getGoldValue().equals("")) {
                if (extractionModelResult.getExtractedValue().isEmpty() || extractionModelResult.getExtractedValue().equals("")) {
                    tn.getAndIncrement();
                } else {
                    fp.getAndIncrement();
                }
            } else {
                if (extractionModelResult.getGoldValue().equals(extractionModelResult.getExtractedValue())) {
                    tp.getAndIncrement();
                } else {
                    fn.getAndIncrement();
                }
            }
        });

        precision = tp.get() /(double) (tp.get() + fp.get());
        recall = tp.get() / ((double) tp.get() + fn.get());
        accuracy = (tp.get() + tn.get()) /(double) (tp.get() + fp.get() + tn.get() + fn.get());
        f1 = (2*precision*recall)/(precision+recall);
        return taskInput.asResult()
                .withColumn("tp", tp.toString())
                .withColumn("fp", fp.toString())
                .withColumn("fn", fn.toString())
                .withColumn("tn", tn.toString())
                .withColumn("precision", String.valueOf(precision))
                .withColumn("recall", String.valueOf(recall))
                .withColumn("accuracy", String.valueOf(accuracy))
                .withColumn("f1",String.valueOf(f1));
    }
}
