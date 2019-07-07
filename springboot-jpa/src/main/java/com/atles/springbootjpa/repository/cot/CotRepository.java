package com.atles.springbootjpa.repository.cot;


import com.atles.springbootjpa.entity.Computer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * user@Bright Rain .
 * 2019/7/6.
 */
@Repository
public interface CotRepository extends JpaRepository<Computer,Integer> {
}
