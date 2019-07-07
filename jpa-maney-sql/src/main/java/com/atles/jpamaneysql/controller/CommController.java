package com.atles.jpamaneysql.controller;

import com.atles.jpamaneysql.bean.Product;
import com.atles.jpamaneysql.entity.Computer;
import com.atles.jpamaneysql.service.CommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * user@Bright Rain .
 * 2019/7/6.
 */
@RestController
public class CommController {
    @Autowired
    private  CommService service;

    @PostMapping(value = "/getAllPro")
    public List<Product> getAllPro(){
        return service.getAllPro();
    }


    @GetMapping(value = "/getAllComputer")
    public List<Computer> getAllComputer(){
        return service.getAllComputer();
    }


}
