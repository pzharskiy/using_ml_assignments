package com.workfusion.academy.repository;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.workfusion.academy.model.ExtractionModelResult;
import com.workfusion.odf2.core.orm.OrmLiteRepository;
import com.workfusion.odf2.transaction.repository.TransactionalEntityRepository;

import java.sql.SQLException;
import java.util.List;

public class ExtractionModelResultsRepository {

    private final Dao<ExtractionModelResult, String> dao;

    public ExtractionModelResultsRepository(ConnectionSource connectionSource) throws SQLException {
        dao = DaoManager.createDao(connectionSource, ExtractionModelResult.class);
    }

    public void create(ExtractionModelResult extractionModelResult) throws SQLException {
        dao.create(extractionModelResult);
    }


    public List<ExtractionModelResult> getAll() throws SQLException {
        return dao.queryForAll();
    }
}
