package com.workfusion.academy.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.workfusion.odf2.core.orm.Datastore;
import com.workfusion.odf2.core.orm.DatastoreType;
import lombok.*;

@DatabaseTable(tableName = "extraction_model_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Datastore(type = DatastoreType.NON_VERSIONED)
public class ExtractionModelResult {

    public static final String EXTRACTED_VALUE_COLUMN = "extracted_value";
    public static final String GOLD_VALUE_COLUMN = "gold_value";

    @DatabaseField(columnName = EXTRACTED_VALUE_COLUMN, dataType = DataType.STRING)
    private String extractedValue;
    @DatabaseField(columnName = GOLD_VALUE_COLUMN, dataType = DataType.STRING)
    private String goldValue;
}
