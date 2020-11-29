package com.poupa.attestationdeplacement.dto;

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
        this.reasons = new ArrayList<>();
        this.reasons.add(new Reason("Travail", "travail", 47, 553));
        this.reasons.add(new Reason("Achats et courses", "achats_culturel_cultuel", 47, 482));
        this.reasons.add(new Reason("Santé", "sante", 47, 434));
        this.reasons.add(new Reason("Famille", "famille", 47, 410));
        this.reasons.add(new Reason("Handicap", "handicap", 47, 373));
        this.reasons.add(new Reason("Plein air, sport et animaux", "sport_animaux", 47, 349));
        this.reasons.add(new Reason("Convocation", "convocation", 47, 276));
        this.reasons.add(new Reason("Missions", "missions", 47, 252));
        this.reasons.add(new Reason("Enfants", "enfants", 47, 228));
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
