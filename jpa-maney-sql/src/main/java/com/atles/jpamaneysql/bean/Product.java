package com.atles.jpamaneysql.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * user@Bright Rain .
 * 2019/7/6.
 */
@Entity(name = "product")
public class Product {

    @Id
    private int pid;

    @Column(name = "product_name")
    private String productName;
    @Column(name = "db_source")
    private String dbSource;


    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDbSource() {
        return dbSource;
    }

    public void setDbSource(String dbSource) {
        this.dbSource = dbSource;
    }

    @Override
    public String toString() {
        return "Product{" +
                "pid=" + pid +
                ", productName='" + productName + '\'' +
                ", dbSource='" + dbSource + '\'' +
                '}';
    }
}
