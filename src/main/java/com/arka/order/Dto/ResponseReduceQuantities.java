package com.arka.order.Dto;

public class ResponseReduceQuantities {
    private String messsage;
    private String codResponse;

    public ResponseReduceQuantities() {
    }

    public ResponseReduceQuantities(String messsage, String codResponse) {
        this.messsage = messsage;
        this.codResponse = codResponse;
    }

    public String getMesssage() {
        return messsage;
    }

    public void setMesssage(String messsage) {
        this.messsage = messsage;
    }

    public String getCodResponse() {
        return codResponse;
    }

    public void setCodResponse(String codResponse) {
        this.codResponse = codResponse;
    }
}
