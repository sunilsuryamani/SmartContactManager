package com.contact.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.contact.dao.UserRepository;
import com.contact.model.User;

public class UserDetailsServiceImp implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// how to fecthing user from database
		User userByUseName = userRepository.getUserByUseName(username);
		if(userByUseName==null)
		{
			throw new UsernameNotFoundException("Could not found user !!");
		}
		CustomeUserDetails customeUserDetails= new CustomeUserDetails(userByUseName);
		return customeUserDetails;
	}

}
