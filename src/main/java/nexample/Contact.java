 package nexample;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;

/**
 * Contact type with basic properties, all automatically persisted.
 */
@Entity
public class Contact implements Serializable, Comparable<Contact> {
	/**
	 * Use Hibernate generator rather than database identity or sequence as those trigger an
	 * immediate session flush. 
	 */
	@Id @GeneratedValue(generator="system-hilo")
	@GenericGenerator(name="system-hilo", strategy = "hilo")
	private Integer id;

	@Column(nullable=false)
	private String name;

	/** Maps to a subtable.  */
	@ManyToOne
	private Category category;

	private String phoneNumber;

	@Version
	private Integer version = 0;
	
	public Integer getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	/**
	 * Category for a contact, e.g. Work, Friends, Family.
	 */
	@Entity (name = "ContactsCategory") // Contacts$Category frustrates some DBs
	public static class Category implements Serializable {
		@Id @GeneratedValue(strategy = GenerationType.AUTO)
		private Integer id;
		private String name;

		public Integer getId() {
			return id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		@Override
		public String toString() {
			return name;
		}
	}
	
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	
	/** Sort based on name. */
	public int compareTo(Contact c) {
		return name.compareTo(c.getName());
	}
}