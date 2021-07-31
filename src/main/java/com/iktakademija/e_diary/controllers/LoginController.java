package com.iktakademija.e_diary.controllers;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.e_diary.dto.ChangePasswordDTO;
import com.iktakademija.e_diary.dto.UserLoginDTO;
import com.iktakademija.e_diary.dto.UserTokenDTO;
import com.iktakademija.e_diary.entities.UserEntity;
import com.iktakademija.e_diary.repositories.UserRepository;
import com.iktakademija.e_diary.security.Views;
import com.iktakademija.e_diary.services.UserServiceImp;
import com.iktakademija.e_diary.utils.Encryption;
import com.iktakademija.e_diary.utils.RESTError;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
public class LoginController {

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Value("${spring.security.secret-key}")
	private String securityKey;

	@Value("${spring.security.token-duration}")
	private Integer tokenDuration;

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private UserServiceImp userServiceImp;

	/**
	 * Login
	 * 
	 * @param user
	 * @param result
	 * @return UserTokenDTO
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/login")
	public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO user, BindingResult result) {
		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		UserEntity userEntity = userRepo.findByUsername(user.getUsername());
		if (userEntity != null && Encryption.validatePassword(user.getPassword(), userEntity.getPassword())
				&& userEntity.isActive()) {
			// TODO ako je sve ok napravi token i vrati povratnu vrednost userTokenDTO
			String token = getJWTToken(userEntity);
			UserTokenDTO retVal = new UserTokenDTO(user.getUsername(), "Bearer " + token);
			logger.info("User successfully logged in");
			return new ResponseEntity<UserTokenDTO>(retVal, HttpStatus.OK);

		}
		// u suprotnom vrati 401
		return new ResponseEntity<>("Username/password don't match", HttpStatus.UNAUTHORIZED);
	}

	private String getJWTToken(UserEntity userEntity) {
		List<GrantedAuthority> grantedAuthority = AuthorityUtils
				.commaSeparatedStringToAuthorityList(userEntity.getRole().getRoleName().toString());
		String token = Jwts.builder().setId("softtekJWT").setSubject(userEntity.getUsername())
				.claim("authorities",
						grantedAuthority.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + this.tokenDuration))
				.signWith(SignatureAlgorithm.HS512, this.securityKey).compact();
		return token;

	}

	/**
	 * User can change password
	 * 
	 * @param newPass
	 * @param result
	 * @return message
	 */
	@JsonView(Views.Private.class)
	@Secured({ "ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT", "ROLE_PARENT" })
	@RequestMapping(method = RequestMethod.PUT, value = "/changepassword")
	public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDTO newPass, BindingResult result) {
		try {
			if (result.hasErrors()) {
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}
			String username = userServiceImp.getCurrentUsername();

			UserEntity user = userRepo.findByUsername(username);
			if (Encryption.validatePassword(newPass.getOldPassword(), user.getPassword())) {
				user.setPassword(Encryption.getPasswordEncoded(newPass.getNewPassword()));
				userRepo.save(user);
				logger.info("Password is changed");
				return new ResponseEntity<>("Password is successfully updated", HttpStatus.OK);
			} else {
				return new ResponseEntity<RESTError>(new RESTError(HttpStatus.BAD_REQUEST.value(), "Wrong old password. Try again."),
						HttpStatus.BAD_REQUEST);

			}

		} catch (Exception e) {
			logger.error("Exception executing LoginController.changePassword, " + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(1, "Exception occured: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> findAllUsers() {
		List<UserEntity> users = (List<UserEntity>) userRepo.findAll();
		return new ResponseEntity<List<UserEntity>>(users, HttpStatus.OK);
	}

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
	}

}
