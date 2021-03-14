package com.contact.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import com.contact.helper.Messages;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contact.dao.ContactRespository;
import com.contact.dao.UserRepository;
import com.contact.model.Contact;
import com.contact.model.User;





@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRespository contactRespository;
	
	// method for adding common data response user (email)
	@ModelAttribute
	public void addCommanData(Model model,Principal principle)
	{
		String userName = principle.getName();
		System.out.println("USERNAME "+userName);
		User user = userRepository.getUserByUseName(userName);
		System.out.println(user.toString());
		model.addAttribute("user",user);
	}
	
	//home dashboard handler
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal)
	{
		model.addAttribute("title","User Dashboard Page");
		//get the user details by user name(email)
		return "normaluser/user_dashboard";
	}
	
	//open add form handler
	@RequestMapping(value="/add-control-form",method = RequestMethod.GET)
	public String openAddForm(Model model)
	{
		model.addAttribute("title","Add New Contact");
		model.addAttribute("contact",new Contact());
		return "normaluser/add_control_form";
	}
	//processing contact form
	
	@PostMapping("/process-contact")
	public String processContact(
			@ModelAttribute Contact contact
			,@RequestParam("profileImage") MultipartFile file, 
			Principal principle,
			HttpSession session)
	{
		try {
				String name = principle.getName();
				User user = this.userRepository.getUserByUseName(name);
				//processing and uploading file
				if(file.isEmpty())
				{
					System.out.println("must be rquired");
					contact.setuImage("default.png");
				/*
				 * session.setAttribute("message", new
				 * Messages("Please select image !! Try again..","warning"));
				 */
					//return "normaluser/add_control_form";
					
				}
				else {
					//upload the file in folder and update the file name in database
					contact.setuImage(file.getOriginalFilename());
					
					File saveFile = new ClassPathResource("static/image").getFile();
					Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					System.out.println("file is added");
					//success message.
					
				}
				contact.setUsers(user);
				user.getContacts().add(contact);
				this.userRepository.save(user);
				
				System.out.println("Added to the database");
				
				session.setAttribute("message", new Messages("Your contact is added !! Add more..","success"));
				
				
		} catch (Exception e) {
			System.out.println("Error :"+e.getMessage());
			//error message
			session.setAttribute("message", new Messages("Something went wrong !! Please try again..","danger"));
		}
		return "normaluser/add_control_form";
	}
	//show contacts handler
	@GetMapping("/show-contact/{page}")
	public String showContact(@PathVariable("page") Integer page, Model model,Principal principal)
	{
		String name = principal.getName();
		User userName = this.userRepository.getUserByUseName(name);
		
		model.addAttribute("title","Show contact list");
		
		PageRequest numberOfList = PageRequest.of(page, 2);
		
		Page<Contact> contacts = this.contactRespository.findContactsByUser(userName.getId(),numberOfList);
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPage",contacts.getTotalPages());
		return "normaluser/show_contact";
	}
	
	//showing particular contact details
	@RequestMapping("/contact/{cid}")
	public String showContactDetails(@PathVariable("cid") int cid, Model model,Principal principal)
	{
		System.out.println("C ID "+cid);
		model.addAttribute("title","Contact details");
		Optional<Contact> findById = this.contactRespository.findById(cid);
		Contact contact = findById.get();
		
		//Solving Security Bug
		//first the check current login user then apply the condition
		String userName = principal.getName();
		User currentLoginUser = this.userRepository.getUserByUseName(userName);
		
		if(currentLoginUser.getId()==contact.getUsers().getId())
		{
			model.addAttribute("contacts",contact);
		}
		
		
		return "normaluser/contact_details";
	}
	
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid
			,HttpSession session,Principal principal)
	{
		Contact contact = this.contactRespository.findById(cid).get();
		
		System.out.println("contact delete iid "+contact.getCid());
		
//		contact.setUsers(null);
//		this.contactRespository.delete(contact);
		User user = this.userRepository.getUserByUseName(principal.getName());
		
		user.getContacts().remove(contact);
		
		this.userRepository.save(user);
		
		session.setAttribute("message", new Messages("Your contact delete successfully !!", "success"));
		return "redirect:/user/show-contact/0";
	}
	
	//open update form handler
	
	@PostMapping("/update-contact/{cid}")
	public String updateContact(@PathVariable("cid") Integer cid,Model model)
	{
		Contact contact = this.contactRespository.findById(cid).get();
		model.addAttribute("contacts",contact);
		model.addAttribute("title","Update contact form");
		return "normaluser/update_contact";
	}
	// contact update process form 
	@PostMapping("/process-update-contact")
	public String updateContactProcess(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file
			,Model model,HttpSession session,Principal principal)
	{
			//old contact details
			Contact oldContact = this.contactRespository.findById(contact.getCid()).get();
		try {
			System.out.println("aJDBJ "+contact.getName());
			
			if(!file.isEmpty())
			{
				//file rewrite work
				//delete old profile image
				File deleteFile = new ClassPathResource("static/image").getFile();
				File file1=new File(deleteFile, oldContact.getuImage());
				file1.delete();
				
				//update new profile image
				File saveFile = new ClassPathResource("static/image").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				contact.setuImage(file.getOriginalFilename());
				
			}else {
				contact.setuImage(oldContact.getuImage());
			}
			 
			User user = this.userRepository.getUserByUseName(principal.getName());
			contact.setUsers(user);
			this.contactRespository.save(contact);
			session.setAttribute("message", new Messages("Your contact is updated !!", "success"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "redirect:/user/contact/"+contact.getCid();
	}
	

}
