package com.atles.springbootjpa.service;


import com.atles.springbootjpa.bean.Product;
import com.atles.springbootjpa.entity.Computer;
import com.atles.springbootjpa.repository.cot.CotRepository;
import com.atles.springbootjpa.repository.pro.ProRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * user@Bright Rain .
 * 2019/7/6.
 */
@Service
public class CommService {

    @Autowired
    CotRepository cotRepository;

    @Autowired
    ProRepository proRepository;

    public List<Product> getAllPro(){
        List<Product> all = proRepository.findAll();
        return all;
    }

    public List<Computer> getAllComputer(){
        return cotRepository.findAll();
    }

}
