package example

import java.io.Serializable
import javax.persistence._

import org.hibernate.annotations.Immutable


@Entity
@Immutable
@Table { val name="z_investigators" }
class Contact extends Serializable with Comparable[Contact]
{
  @Id
  @GeneratedValue
  @Column { val name="investigator_id" }
  var id: java.lang.Long = _
  
  @Embedded
  var name: Name = _
  
  @Embedded
  var streetAddress: StreetAddress = _
  
  var phone: String = _
  
  @Column { val insertable=false, val updatable=false }
  var email: String = _

  var fax: String = _
  
  @ManyToOne
  @JoinColumn { val name="email", val referencedColumnName="email" }
  var login: Login = _


  def compareTo(c: Contact) = { 
    var ret = name.compareTo(c.name)
    if (ret == 0) {
      ret = streetAddress.compareTo(c.streetAddress)
      if (ret == 0) {	
    	ret = email.compareTo(c.email)
    	if (ret == 0) {
    	  ret = phone.compareTo(c.phone)
    	}
      }
    }
    ret
  }

      
  override def equals(other: Any) = {
    val cother = other.asInstanceOf[Contact]
    email.equals(cother.email)
  }
  
  override def hashCode = email.hashCode
  
}


@Embeddable
class Name extends Serializable with Comparable[Name]
{
  @Column { val name="name_first" }
  var first: String = _
  
  @Column { val name="name_last" }
  var last: String = _
  
  override def compareTo(other: Name) = {
    var ret = last.compareTo(other.last)
    if (ret == 0) ret = first.compareTo(other.first)
    ret
  }
  
  override def equals(other: Any) = {
    val nother = other.asInstanceOf[Name]
    first == nother.first && last == nother.last
  }
  
  override def hashCode = {
    first.hashCode * 42 + last.hashCode
  }
  
  override def toString = {
    last + ", " + first
  }
  
}


@Embeddable
class StreetAddress extends Serializable with Comparable[StreetAddress]
{
  @Column { val name="addr_line_1" }
  var first: String = _
  
  @Column { val name="addr_line_2" }
  var second: String = _
  
  @Column { val name="addr_line_3" }
  var third: String = _

  var city:       String = _
  var state:      String = _
  @Column { val name="postal_code" }
  var postalCode: String = _
  var country:    String = _

  
  def compareTo(other: StreetAddress) = {
    var ret = first.compareTo(other.first)
    if (ret == 0) {
      ret = second.compareTo(other.second)
      if (ret == 0)  {
        ret = third.compareTo(other.third)
        if (ret == 0) {
          ret = city.compareTo(other.city)
          if (ret == 0) {
            ret = state.compareTo(other.state)
            if (ret == 0) {
              ret = postalCode.compareTo(other.postalCode)
              if (ret == 0) {
                ret = country.compareTo(other.country)
              }
            }
          }
        }
      }
    }
    ret
  }
  
  override def equals(oth: Any) = {
    val other = oth.asInstanceOf[StreetAddress]
    first == other.first &&
    second == other.second &&
    third == other.third && 
    city == other.city &&
    state == other.state && 
    postalCode == other.postalCode &&
    country == other.country
  }
  
  override def hashCode = {
    first.hashCode * 422 + second.hashCode * 42 + third.hashCode + 
    city.hashCode * 777 + state.hashCode * 77 + postalCode.hashCode * 7 + country.hashCode
  }
  
  
}

