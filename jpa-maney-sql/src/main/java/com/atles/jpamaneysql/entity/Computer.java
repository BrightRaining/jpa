package com.atles.jpamaneysql.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * user@Bright Rain .
 * 2019/7/6.
 */
@Entity(name = "computer")
public class Computer {

    @Id
    private Integer computerId;

    private String computerName;

    private String computerPrice;

    private String computerType;

    public Integer getComputerId() {
        return computerId;
    }

    public void setComputerId(Integer computerId) {
        this.computerId = computerId;
    }

    public String getComputerName() {
        return computerName;
    }

    public void setComputerName(String computerName) {
        this.computerName = computerName;
    }

    public String getComputerPrice() {
        return computerPrice;
    }

    public void setComputerPrice(String computerPrice) {
        this.computerPrice = computerPrice;
    }

    public String getComputerType() {
        return computerType;
    }

    public void setComputerType(String computerType) {
        this.computerType = computerType;
    }

    @Override
    public String toString() {
        return "Computer{" +
                "computerId=" + computerId +
                ", computerName='" + computerName + '\'' +
                ", computerPrice='" + computerPrice + '\'' +
                ", computerType='" + computerType + '\'' +
                '}';
    }
}
