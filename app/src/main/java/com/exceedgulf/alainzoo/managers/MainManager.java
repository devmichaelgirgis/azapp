package com.exceedgulf.alainzoo.managers;

import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.database.AlainZooDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Response;

/**
 * Created by ehab.alagoza on 12/27/2017.
 */

public interface MainManager<T> {

    AlainZooDB alainZooDB = AlainZooDB.getInstance();

    boolean insertEntitiesToDB(List<T> entities);

    boolean insertEntityToDB(T entity);

    List<T> getAllEntitiesFromDB(final int pageNumber);

    List<T> getTopThreeEntities();

    void getAllEntitiesData(final int pageNumber, final ApiCallback apiCallback, final boolean isHome);

    void filterEntitiesData(final int pageNumber, int categeoryId, final ApiCallback apiCallback);

    boolean checkValidateResponce(final Response<ArrayList<T>> response);

    T getEntityDetails(final Integer id,final ApiDetailCallback apiDetailCallback);

    T getEntityDetailsFromDB(final Integer id);

    boolean isOlderData();
}
