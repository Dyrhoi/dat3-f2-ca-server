package dtos;

import entities.CityInfo;

public class CityInfoDTO {

    private String postalcode;
    private String city;

    public CityInfoDTO(String postalcode, String city) {
        this.postalcode = postalcode;
        this.city = city;
    }


    public CityInfoDTO(CityInfo cityInfo) {
        this.postalcode =cityInfo.getPostalCode();
        this.city = cityInfo.getCity();
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
