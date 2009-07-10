package example;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.annotations.ForceDiscriminator;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role_nam", discriminatorType = DiscriminatorType.STRING)
@ForceDiscriminator
@Table(name="z_roles")
public abstract class Role implements Comparable<Role>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6318360522731138654L;
	
	@Id
	@GeneratedValue
	@Column(name="role_id", insertable=false, updatable=false)
	private Long id;
	
	@Column(name="role_nam", insertable=false, updatable=false)
	private String name;

	public int compareTo(Role o) {
		return name.compareTo(o.getName());
	}

	
	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof Role)) return false;
		Role r = (Role) other;
		return (name.equals(r.getName()));
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}


