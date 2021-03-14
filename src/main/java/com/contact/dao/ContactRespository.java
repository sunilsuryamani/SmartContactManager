package com.contact.dao;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contact.model.Contact;

public interface ContactRespository extends JpaRepository<Contact, Integer> {
	
	
	//pageable contain the current page-page and also contant the number of per page-page;
	@Query("from Contact as c where c.users.id=:userId")
	public Page<Contact> findContactsByUser(@Param("userId") int userId,Pageable pageable);
}
