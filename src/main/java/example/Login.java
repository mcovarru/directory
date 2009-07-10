package example;


import java.io.Serializable;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;


/**
 * This is written in Java rather than Scala since 2.7.x Scala does not
 * supported nested annotations that we need to express many-to-many
 * relationships in JPA.  Supposedly this will be remedied in Scala 2.8.x.
 * 
 * @author mcovarru
 *
 */
@Entity
@Immutable
@Table(name="z_logins")
public class Login implements Serializable, Comparable<Login> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7273777354238639330L;

	
	@Id
	@GeneratedValue
	@Column(name="login_id")
	private Long id;

	@Column(name="login_nam")
	private String name;
	
	@Column(name="login_pwd")
	private String password;
	
	private String email;
	

	@ManyToMany(targetEntity=Role.class)
	@JoinTable(name="z_login_role_xref",
			joinColumns=@JoinColumn(name="login_id"),
			inverseJoinColumns=@JoinColumn(name="role_id"))
	private Set<Role> roles;

	public int compareTo(Login o) {
		return name.compareTo(o.getName());
	}
	
	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null) return false;
		if (!(o instanceof Login)) return false;
		return (name.equals(((Login) o).getName()));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}	


}
