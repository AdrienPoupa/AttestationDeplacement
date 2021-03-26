package com.poupa.attestationdeplacement.dto;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Attestation Data
 */
public abstract class Attestation {
    protected int id;
    protected String surname;
    protected String lastName;
    protected String city;
    protected String postalCode;
    protected String address;
    protected String birthPlace;
    protected String birthDate;
    protected String travelDate;
    protected String travelHour;
    protected String hour;
    protected String minute;

    protected List<Reason> reasons;

    public Attestation() {
        setupReasons();
    }

    protected abstract void setupReasons();

    public abstract String getPdfFileName();

    public abstract String getReasonPrefix();

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

    public abstract String toString();
}
