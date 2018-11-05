package com.exceedgulf.alainzoo.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.database.dao.AboutTermsPrimacyDao;
import com.exceedgulf.alainzoo.database.dao.AnimalsCategoryDao;
import com.exceedgulf.alainzoo.database.dao.AnimalsDao;
import com.exceedgulf.alainzoo.database.dao.AttractionsDao;
import com.exceedgulf.alainzoo.database.dao.AvatarsDao;
import com.exceedgulf.alainzoo.database.dao.BeaconsDao;
import com.exceedgulf.alainzoo.database.dao.CameraFrameDao;
import com.exceedgulf.alainzoo.database.dao.ContactusDao;
import com.exceedgulf.alainzoo.database.dao.EducationDao;
import com.exceedgulf.alainzoo.database.dao.EmiratesDao;
import com.exceedgulf.alainzoo.database.dao.ExperienceDao;
import com.exceedgulf.alainzoo.database.dao.ExploreZooDao;
import com.exceedgulf.alainzoo.database.dao.FAQsDao;
import com.exceedgulf.alainzoo.database.dao.FamilyDao;
import com.exceedgulf.alainzoo.database.dao.FeedBackCategoryDao;
import com.exceedgulf.alainzoo.database.dao.GamesDao;
import com.exceedgulf.alainzoo.database.dao.MyPlanDao;
import com.exceedgulf.alainzoo.database.dao.MyPlaneVisitedItemDao;
import com.exceedgulf.alainzoo.database.dao.NationalitiesDao;
import com.exceedgulf.alainzoo.database.dao.OpeningHoursDao;
import com.exceedgulf.alainzoo.database.dao.OpeningHoursFrontDao;
import com.exceedgulf.alainzoo.database.dao.ShuttleServiceDao;
import com.exceedgulf.alainzoo.database.dao.VisitorServiceDao;
import com.exceedgulf.alainzoo.database.dao.WhatsNewDao;
import com.exceedgulf.alainzoo.database.models.AboutTermsPrivacyModel;
import com.exceedgulf.alainzoo.database.models.Animal;
import com.exceedgulf.alainzoo.database.models.AnimalsCategory;
import com.exceedgulf.alainzoo.database.models.Attraction;
import com.exceedgulf.alainzoo.database.models.Avatars;
import com.exceedgulf.alainzoo.database.models.BeaconModel;
import com.exceedgulf.alainzoo.database.models.CameraFrame;
import com.exceedgulf.alainzoo.database.models.Contactus;
import com.exceedgulf.alainzoo.database.models.Education;
import com.exceedgulf.alainzoo.database.models.Emirates;
import com.exceedgulf.alainzoo.database.models.Experience;
import com.exceedgulf.alainzoo.database.models.ExploreZoo;
import com.exceedgulf.alainzoo.database.models.FAQs;
import com.exceedgulf.alainzoo.database.models.Family;
import com.exceedgulf.alainzoo.database.models.FeedbackCategory;
import com.exceedgulf.alainzoo.database.models.Games;
import com.exceedgulf.alainzoo.database.models.MyPlaneVisitedItem;
import com.exceedgulf.alainzoo.database.models.Nationalities;
import com.exceedgulf.alainzoo.database.models.OpeningHours;
import com.exceedgulf.alainzoo.database.models.ShuttleService;
import com.exceedgulf.alainzoo.database.models.VisitorService;
import com.exceedgulf.alainzoo.database.models.WhatsNew;
import com.exceedgulf.alainzoo.models.GeneralPlan;
import com.exceedgulf.alainzoo.models.OpeningHourFront;
import com.exceedgulf.alainzoo.models.RecommendedPlanModel;

/**
 * Created by ehab.alagoza on 12/26/2017.
 */

@Database(version = 1, entities = {Animal.class, AnimalsCategory.class, BeaconModel.class, Experience.class, FAQs.class, GeneralPlan.class, Attraction.class, WhatsNew.class, Education.class, VisitorService.class, OpeningHours.class, Avatars.class, FeedbackCategory.class, Games.class, AboutTermsPrivacyModel.class, Contactus.class, Emirates.class, Nationalities.class, ExploreZoo.class, OpeningHourFront.class, ShuttleService.class, MyPlaneVisitedItem.class, Family.class, CameraFrame.class, RecommendedPlanModel.class})
public abstract class AlainZooDB extends RoomDatabase {
    private static final String DATABASE_NAME = "alainzoo";
    private static AlainZooDB alainZooDB = null;

    private static AlainZooDB buildDatabase(final Context appContext) {
        alainZooDB = Room.databaseBuilder(appContext,
                AlainZooDB.class, DATABASE_NAME).allowMainThreadQueries().build();
        return alainZooDB;
    }

    public static AlainZooDB getInstance() {
        if (alainZooDB == null) {
            alainZooDB = buildDatabase(AppAlainzoo.getAppAlainzoo());
        }
        return alainZooDB;
    }

    public abstract AnimalsCategoryDao animalsCategoryDao();

    public abstract AnimalsDao animalsDao();

    public abstract BeaconsDao beaconsDao();

    public abstract ShuttleServiceDao shuttleServiceDao();

    public abstract ExperienceDao experienceDao();

    public abstract WhatsNewDao whatsNewDao();

    public abstract AttractionsDao attractionsDao();

    public abstract FAQsDao faQsDao();

    public abstract EducationDao educationDao();

    public abstract VisitorServiceDao visitorServiceDao();

    public abstract OpeningHoursDao openingHoursDao();

    public abstract AvatarsDao avatarsDao();

    public abstract FeedBackCategoryDao feedBackCategoryDao();

    public abstract GamesDao gamesDao();

    public abstract AboutTermsPrimacyDao aboutTermsPrimacyDao();

    public abstract ContactusDao contactusDao();

    public abstract EmiratesDao emiratesDao();

    public abstract NationalitiesDao nationalitiesDao();

    public abstract ExploreZooDao exploreZooDao();

    public abstract OpeningHoursFrontDao openingHoursFrontDao();

    public abstract MyPlaneVisitedItemDao myPlaneVisitedItemDao();

    public abstract FamilyDao familyDao();

    public abstract CameraFrameDao cameraFrameDao();

    public abstract MyPlanDao myPlanDao();

}
