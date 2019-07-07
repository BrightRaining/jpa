package com.atles.springbootjpa.repository.pro;

import com.atles.springbootjpa.bean.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * user@Bright Rain .
 * 2019/7/6.
 */
@Repository
public interface ProRepository extends JpaRepository<Product,Integer> {
}
