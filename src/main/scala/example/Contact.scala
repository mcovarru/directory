package example

import java.io.Serializable
import javax.persistence._


@Entity
@Table { val name="z_investigators" }
class Contact extends Serializable with Comparable[Contact]
{
  @Id
  @GeneratedValue
  @Column { val name="investigator_id" }
  var id: java.lang.Integer = _
  
  @Embedded
  var name: Name = _
  
  @Column { val name="phone" }
  var phoneNumber: String = _
  
  def compareTo(c: Contact) = { 
    val nameCmp = name.compareTo(c.name)
    if (nameCmp == 0) phoneNumber.compareTo(c.phoneNumber) else nameCmp
  }
  
  override def equals(other: Any) = {
    val cother = other.asInstanceOf[Contact]
    name == cother.name && phoneNumber == cother.phoneNumber
  }
  
  override def hashCode = {
    name.hashCode * 42 + phoneNumber.hashCode
  }
  
}


@Embeddable
class Name extends Serializable with Comparable[Name]
{
  @Column { val name="name_first" }
  var first: String = _
  
  @Column { val name="name_last" }
  var last: String = _
  
  override def compareTo(other: Name) = {
    val nfirst = first.compareTo(other.first)
    if (nfirst == 0) last.compareTo(other.last) else nfirst
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

