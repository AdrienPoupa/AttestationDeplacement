package com.poupa.attestationdeplacement.db;

import android.app.Application;

import androidx.annotation.NonNull;

import java.util.List;

public class ProfileViewModel {
    private final ProfileRepository profileRepository;
    private final List<ProfileEntity> allProfile;

    public ProfileViewModel(@NonNull Application application) {
        this.profileRepository = new ProfileRepository(application);
        this.allProfile = profileRepository.getAllProfile();
    }

    public List<ProfileEntity> getAllProfile() {
        return allProfile;
    }

    public ProfileEntity getById(int id) {
        return this.profileRepository.getById(id);
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
