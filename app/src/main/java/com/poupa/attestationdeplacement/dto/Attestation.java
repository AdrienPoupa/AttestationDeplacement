package com.poupa.attestationdeplacement.dto;

import android.content.Context;

import com.poupa.attestationdeplacement.MainActivity;
import com.poupa.attestationdeplacement.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Attestation Data
 */
public class Attestation {
    private int id;
    private String surname;
    private String lastName;
    private String city;
    private String postalCode;
    private String address;
    private String birthPlace;
    private String birthDate;
    private String travelDate;
    private String travelHour;
    private String hour;
    private String minute;

    private List<Reason> reasons;

    public Attestation() {
        setupReasons();
    }

    private void setupReasons() {
        Context context = MainActivity.getContext();

        this.reasons = new ArrayList<>();
        // Curfew reasons
        this.reasons.add(new Reason(context.getString(R.string.reason1_smalltext), "travail", 60, 550, 1));
        this.reasons.add(new Reason(context.getString(R.string.reason2_smalltext), "sante", 60, 488, 1));
        this.reasons.add(new Reason(context.getString(R.string.reason3_smalltext), "famille", 60, 412, 1));
        this.reasons.add(new Reason(context.getString(R.string.reason4_smalltext), "handicap", 60, 350, 1));
        this.reasons.add(new Reason(context.getString(R.string.reason5_smalltext), "convocation", 60, 300, 1));
        this.reasons.add(new Reason(context.getString(R.string.reason6_smalltext), "missions", 60, 225, 1));
        this.reasons.add(new Reason(context.getString(R.string.reason7_smalltext), "transits", 60, 170, 1));
        this.reasons.add(new Reason(context.getString(R.string.reason8_smalltext), "animaux", 60, 115, 1));
        // March 2021 lockdown reasons
        this.reasons.add(new Reason(context.getString(R.string.reason9_smalltext), "achats_pro", 60, 582, 2));
        this.reasons.add(new Reason(context.getString(R.string.reason10_smalltext), "achats", 60, 527, 2));
        this.reasons.add(new Reason(context.getString(R.string.reason11_smalltext), "demenagement", 60, 480, 2));
        this.reasons.add(new Reason(context.getString(R.string.reason12_smalltext), "sport", 60, 410, 2));
        this.reasons.add(new Reason(context.getString(R.string.reason13_smalltext), "demarches", 60, 328, 2));
        this.reasons.add(new Reason(context.getString(R.string.reason14_smalltext), "culte", 60, 272, 2));
        this.reasons.add(new Reason(context.getString(R.string.reason15_smalltext), "rassemblements", 60, 237, 2));

    }

    /**
     * Get the full address
     * @return
     */
    public String getFullAddress() {
        return String.format("%s %s %s", address, postalCode, city);
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(String travelDate) {
        this.travelDate = travelDate;
    }

    public String getTravelHour() {
        return travelHour;
    }

    public void setTravelHour(String travelHour) {
        this.travelHour = travelHour;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getReasonsQrCode() {
        return getEnabledReasons().stream()
                .map(Reason::getQrCodeName)
                .collect(Collectors.joining(", "));
    }

    public String getReasonsDatabase() {
        return getEnabledReasons().stream()
                .map(Reason::getDatabaseName)
                .collect(Collectors.joining(", "));
    }

    public List<Reason> getEnabledReasons() {
        return this.reasons.stream()
                .filter(Reason::isEnabled)
                .collect(Collectors.toList());
    }

    public List<Reason> getReasons() {
        return reasons;
    }

    public void setReasons(List<Reason> reasons) {
        this.reasons = reasons;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
