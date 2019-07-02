package com.loanhome.lib.bean;

/**
 * @Description Created by Don on 2019/7/1
 */
public class IDCardInfo {
    private int side;
    private String idCardName;
    private String idCardNumber;
    private String address;

    private String validDate;
    private String issuedBy;

    private String frontImages;
    private String backImages;

    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }

    public String getIdCardName() {
        return idCardName;
    }

    public void setIdCardName(String idCardName) {
        this.idCardName = idCardName;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getValidDate() {
        return validDate;
    }

    public void setValidDate(String validDate) {
        this.validDate = validDate;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public String getFrontImages() {
        return frontImages;
    }

    public void setFrontImages(String frontImages) {
        this.frontImages = frontImages;
    }

    public String getBackImages() {
        return backImages;
    }

    public void setBackImages(String backImages) {
        this.backImages = backImages;
    }
}
