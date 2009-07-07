package example

import java.io.Serializable
import javax.persistence._
import org.hibernate.annotations.GenericGenerator


@Entity
class Contact extends Serializable with Comparable[Contact]
{
  @Id
  @GeneratedValue { val generator = "system-hilo" }
  @GenericGenerator{ val name = "system-hilo", val strategy = "hilo"}
  var id: java.lang.Integer = _
  
  var name: String = _
  
  var phoneNumber: String = _
  
  @Version
  var version: java.lang.Integer = _
  
  @ManyToOne
  var category: Category = _
  
  def compareTo(c: Contact) = name.compareTo(c.name)
  

}

@Entity { val name="ContactsCategory" }
class Category extends Serializable
{
  @Id @GeneratedValue
  var id: java.lang.Integer = _
  
  var name: String = _
  
  override def toString = name
}
