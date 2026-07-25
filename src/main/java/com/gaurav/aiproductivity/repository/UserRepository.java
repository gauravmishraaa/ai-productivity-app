package com.gaurav.aiproductivity.repository;

import com.gaurav.aiproductivity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}