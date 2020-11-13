package com.poupa.attestationdeplacement.db;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

public class ProfileRepository {
    private final ProfileDao profileDao;
    private final List<ProfileEntity> allProfile;

    public ProfileRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        this.profileDao = database.profileDao();
        this.allProfile = profileDao.getAll();
    }

    public void insert(ProfileEntity profileEntity) {
        new InsertAsyncTask(profileDao).execute(profileEntity);
    }

    public void update(ProfileEntity profileEntity) {
        new UpdateAsyncTask(profileDao).execute(profileEntity);
    }

    public void delete(ProfileEntity profileEntity) {
        new DeleteAsyncTask(profileDao).execute(profileEntity);
    }

    public ProfileEntity getById(int id) {
        for (ProfileEntity elt : getAllProfile()) {
            if (elt.getId() == id) {
                return elt;
            }
        }
        return null;
    }

    public List<ProfileEntity> getAllProfile() {
        return allProfile;
    }


    private static class InsertAsyncTask extends AsyncTask<ProfileEntity, Void, Void> {
        private final ProfileDao profileDao;

        public InsertAsyncTask(ProfileDao profileDao) {
            this.profileDao = profileDao;
        }

        @Override
        protected Void doInBackground(ProfileEntity... profileEntities) {
            ProfileEntity profileEntity = profileEntities[0];
            this.profileDao.insert(profileEntity);
            return null;
        }
    }

    private static class UpdateAsyncTask extends AsyncTask<ProfileEntity, Void, Void> {
        private final ProfileDao profileDao;

        public UpdateAsyncTask(ProfileDao profileDao) {
            this.profileDao = profileDao;
        }

        @Override
        protected Void doInBackground(ProfileEntity... profileEntities) {
            ProfileEntity loginEntity = profileEntities[0];
            this.profileDao.update(loginEntity);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<ProfileEntity, Void, Void> {
        private final ProfileDao profileDao;

        public DeleteAsyncTask(ProfileDao profileDao) {
            this.profileDao = profileDao;
        }

        @Override
        protected Void doInBackground(ProfileEntity... profileEntities) {
            ProfileEntity loginEntity = profileEntities[0];
            this.profileDao.delete(loginEntity);
            return null;
        }
    }
}
