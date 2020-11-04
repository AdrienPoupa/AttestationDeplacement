package com.poupa.attestationdeplacement.db;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ProfileViewModel {
    private ProfileRepository profileRepository;
    private LiveData<List<ProfileEntity>> allEntities;

    public ProfileViewModel(@NonNull Application application) {
        this.profileRepository = new ProfileRepository(application);
        this.allEntities = profileRepository.getAllProfile();
    }

    public LiveData<List<ProfileEntity>> getAllEntities() {
        return allEntities;
    }

    public void insert(ProfileEntity profileEntity) {
        this.profileRepository.insert(profileEntity);
    }

    public void update(ProfileEntity profileEntity) {
        this.profileRepository.update(profileEntity);
    }

    public void delete(ProfileEntity profileEntity) {
        this.profileRepository.update(profileEntity);
    }
}
