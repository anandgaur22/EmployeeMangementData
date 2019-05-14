package com.employee.employee.model;

public class CarListModel {

    private String car_id;
    private String car_no;
    private String car_make;
    private String car_engine;
    private String car_chasis_no;
    private String customer_name;


    public String getCustomer_name() {
        return customer_name;
    }
    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCar_id() {
        return car_id;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }

    public String getCar_no() {
        return car_no;
    }

    public void setCar_no(String car_no) {
        this.car_no = car_no;
    }

    public String getCar_make() {
        return car_make;
    }

    public void setCar_make(String car_make) {
        this.car_make = car_make;
    }

    public String getCar_engine() {
        return car_engine;
    }

    public void setCar_engine(String car_engine) {
        this.car_engine = car_engine;
    }

    public String getCar_chasis_no() {
        return car_chasis_no;
    }

    public void setCar_chasis_no(String car_chasis_no) {
        this.car_chasis_no = car_chasis_no;
    }
}
