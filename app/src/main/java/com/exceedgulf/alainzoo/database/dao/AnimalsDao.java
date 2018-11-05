package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.database.models.Animal;

import java.util.List;

/**
 * Created by ehab.alagoza on 12/26/2017.
 */
@Dao
public interface AnimalsDao {


    @Query("SELECT * from animal where language = (:languagecode) and category_id = (:categoryId) ORDER BY id DESC limit 10 offset (:pageNumber*10)")
    List<Animal> getAllAnimals(final String languagecode, final int pageNumber, int categoryId);

    @Query("SELECT * from animal where language = (:languagecode) and category_id = (:categoryId) ORDER BY id DESC ")
    List<Animal> getAllAnimalsByCategoryId(final String languagecode, int categoryId);

    @Query("SELECT * from animal where language = (:languagecode) and name = (:name) ORDER BY id DESC limit 10 offset (:pageNumber*10)")
    List<Animal> getAllAnimals(final String languagecode, final int pageNumber, String name);

    @Query("SELECT * FROM animal where language = (:languagecode) ORDER BY id DESC ")
    List<Animal> getAll(final String languagecode);

    @Query("SELECT * FROM animal WHERE id =(:id) AND language = (:languagecode) LIMIT 1")
    Animal findById(int id, String languagecode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplaceAll(List<Animal> animals);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplace(Animal animal);
    // date(creation_date) = date('now')

    @Query("select (CASE WHEN date((select date(creation_date) from animal where language = (:languagecode) ORDER BY date(creation_date) ASC limit 1)) = date('now','localtime') THEN 1 ELSE 0 END) isolder")
    int isOlder(final String languagecode);
}
