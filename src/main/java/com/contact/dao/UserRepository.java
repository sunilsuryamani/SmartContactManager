package com.contact.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contact.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

		@Query("Select u from User u where u.email=:email")
		public User getUserByUseName(@Param("email") String email);
		
}
