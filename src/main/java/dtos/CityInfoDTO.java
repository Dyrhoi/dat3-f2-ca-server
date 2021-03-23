package dtos;

import entities.CityInfo;

public class CityInfoDTO {

    private int postalcode;
    private String city;

    public CityInfoDTO(int postalcode, String city) {
        this.postalcode = postalcode;
        this.city = city;
    }


    public CityInfoDTO(CityInfo cityInfo) {
        this.postalcode =cityInfo.getPostalCode();
        this.city = cityInfo.getCity();
    }

    public int getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(int postalcode) {
        this.postalcode = postalcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
