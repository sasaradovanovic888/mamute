package br.com.caelum.brutal.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import br.com.caelum.brutal.infra.Digester;
import br.com.caelum.brutal.integracao.dao.Identifiable;

@Entity
public class User implements Identifiable {

	@Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
	private final DateTime createdAt = new DateTime();

	@Column(unique=true)
	private String email;

	@Id
	@GeneratedValue
	private Long id;

	@NotEmpty
	private String password = "";
	
	@NotEmpty
	@Type(type = "text")
	private String name;
	
	private long karma = 0;
	
	private boolean moderator = false;

	private String forgotPasswordToken = "";
	
	/**
	 * @deprecated hibernate eyes only
	 */
	protected User() {
		this("", "", "786213675312678");
	}

	public User(String name, String email, String password) {
		super();
		this.email = email;
		this.name = name;
		this.password = Digester.encrypt(password);
	}

	public Long getId() {
		return id;
	}
	
	public String getPhoto() {
		return "http://www.gravatar.com/avatar/" + Digester.md5(email);
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "[User " + email + ", "+ name +"]";
	}
	
	public long getKarma() {
		return karma;
	}
	
	public String getEmail() {
		return email;
	}

	public boolean isModerator() {
		return moderator;
	}

	public User asModerator() {
		this.moderator = true;
		return this;
	}
	
	public String touchForgotPasswordToken () {
		this.forgotPasswordToken = Double.toString(Math.random());
		return this.forgotPasswordToken;
	}

	public boolean isValidForgotPasswordToken(String token) {
		return this.forgotPasswordToken.equals(token);
	}

	public boolean updateForgottenPassword(String password,
			String password_confirmation) {
		if(password.equals(password_confirmation)) {
			this.password = br.com.caelum.brutal.infra.Digester.encrypt(password);
			touchForgotPasswordToken();
			return true;
		}
		return false;
	}
}
