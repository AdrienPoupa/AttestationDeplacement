package com.poupa.attestationdeplacement.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "profiles")
public class ProfileEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "firstname")
    private String firstname;
    @ColumnInfo(name = "lastname")
    private String lastname;
    @ColumnInfo(name = "birthdate")
    private String birthdate;
    @ColumnInfo(name = "birthplace")
    private String birthplace;
    @ColumnInfo(name = "address")
    private String address;
    @ColumnInfo(name = "postalcode")
    private String postalcode;
    @ColumnInfo(name = "city")
    private String city;

    /**
     * Constructor for profile entity
     *
     * @param firstname  : the firstname of the user
     * @param lastname   : the lastname of the user
     * @param birthdate  : the birthdate of the user
     * @param birthplace : the birthplace of the user
     * @param address    : the current address of the user
     * @param postalcode : the postal code of the user's address
     * @param city       : the city of the user's address
     */
    public ProfileEntity(@NonNull String firstname, @NonNull String lastname,
                         @NonNull String birthdate, @NonNull String birthplace,
                         @NonNull String address, @NonNull String postalcode,
                         @NonNull String city) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthdate = birthdate;
        this.birthplace = birthplace;
        this.address = address;
        this.postalcode = postalcode;
        this.city = city;
    }

    /* ALL THE SETTERS */

    public void setId(int id) {
        this.id = id;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    /* ALL THE GETTERS */

    public int getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public String getCity() {
        return city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileEntity that = (ProfileEntity) o;
        return firstname.equals(that.firstname) &&
                lastname.equals(that.lastname);
    }

    @Override
    public int hashCode() {
        return id + firstname.hashCode() + lastname.hashCode() +
                birthdate.hashCode() + birthplace.hashCode() +
                address.hashCode() + postalcode.hashCode() +
                city.hashCode();
    }

    @Override
    public String toString() {
        return "ProfileEntity{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", birthplace='" + birthplace + '\'' +
                ", address='" + address + '\'' +
                ", countrycode='" + postalcode + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
