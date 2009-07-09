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


/**
 * This is written in Java rather than Scala since 2.7.x Scala does not
 * supported nested annotations that we need to express many-to-many
 * relationships in JPA.  Supposedly this will be remedied in Scala 2.8.x.
 * 
 * @author mcovarru
 *
 */
@Entity
public class Login implements Serializable, Comparable<Login> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7273777354238639330L;

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

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}
	
	@Id
	@GeneratedValue
	@Column(name="login_id")
	private Long id;

	@Column(name="login_nam")
	private String name;
	
	@Column(name="login_pwd")
	private String password;
	

	@ManyToMany(targetEntity=Role.class)
	@JoinTable(name="z_login_role_xref",
			joinColumns=@JoinColumn(name="role_id"),
			inverseJoinColumns=@JoinColumn(name="login_id"))
	private Set<String> roles;

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
		if (!(o instanceof Login)) return false;
		Login l = (Login) o;
		return (name.equals(l.getName()));
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

}
