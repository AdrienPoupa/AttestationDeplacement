package com.poupa.attestationdeplacement.generator;

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
    private String currentDate;
    private String currentTime;

    private StringBuilder motivesQrCode;
    private StringBuilder motivesDatabase;
    private boolean isReason1;
    private boolean isReason2;
    private boolean isReason3;
    private boolean isReason4;
    private boolean isReason5;
    private boolean isReason6;
    private boolean isReason7;
    private boolean isReason8;

    public Attestation() {
        this.motivesDatabase = new StringBuilder();
        this.motivesQrCode = new StringBuilder();
    }

    /**
     * Get the full address
     * @return
     */
    public String getFullAddress() {
        return String.format("%s %s %s", address, postalCode, city);
    }

    /**
     * Add motives
     * @param motive motive to add
     */
    public void addMotive(String motive) {
        if (motivesQrCode.length() != 0) {
            motivesQrCode.append("-");
            motivesDatabase.append(", ");
        }
        motivesQrCode.append(motive);

        // Capitalize first letter
        motivesDatabase.append(motive.substring(0, 1).toUpperCase()).append(motive.substring(1));
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

    public StringBuilder getMotivesQrCode() {
        return motivesQrCode;
    }

    public void setMotivesQrCode(StringBuilder motivesQrCode) {
        this.motivesQrCode = motivesQrCode;
    }

    public StringBuilder getMotivesDatabase() {
        return motivesDatabase;
    }

    public void setMotivesDatabase(StringBuilder motivesDatabase) {
        this.motivesDatabase = motivesDatabase;
    }

    public boolean isReason1() {
        return isReason1;
    }

    public void setReason1(boolean reason1) {
        isReason1 = reason1;
    }

    public boolean isReason2() {
        return isReason2;
    }

    public void setReason2(boolean reason2) {
        isReason2 = reason2;
    }

    public boolean isReason3() {
        return isReason3;
    }

    public void setReason3(boolean reason3) {
        isReason3 = reason3;
    }

    public boolean isReason4() {
        return isReason4;
    }

    public void setReason4(boolean reason4) {
        isReason4 = reason4;
    }

    public boolean isReason5() {
        return isReason5;
    }

    public void setReason5(boolean reason5) {
        isReason5 = reason5;
    }

    public boolean isReason6() {
        return isReason6;
    }

    public void setReason6(boolean reason6) {
        isReason6 = reason6;
    }

    public boolean isReason7() {
        return isReason7;
    }

    public void setReason7(boolean reason7) {
        isReason7 = reason7;
    }

    public boolean isReason8() {
        return isReason8;
    }

    public void setReason8(boolean reason8) {
        isReason8 = reason8;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
