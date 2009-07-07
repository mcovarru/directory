package example

import java.io.Serializable
import javax.persistence._
import org.hibernate.annotations.GenericGenerator

// members annotated with @BeanProperty will have Java-style getters and setters
// synthesized for them, which we need at least for integration with our own Java
// code until that gets converted over to Scala.  After that, maybe we can remove
// these annotations?  Will Hibernate read/write directly from/to our fields?
import scala.reflect.BeanProperty

@Entity
class Contact extends Serializable with Comparable[Contact]
{
  @Id
  @GeneratedValue { val generator = "system-hilo" }
  @GenericGenerator{ val name = "system-hilo", val strategy = "hilo"}
  @BeanProperty
  var id: java.lang.Integer = _
  
  @BeanProperty
  var name: String = _
  
  @BeanProperty
  var phoneNumber: String = _
  
  @Version
  var version: java.lang.Integer = _
  
  @BeanProperty @ManyToOne
  var category: Category = _
  
  def compareTo(c: Contact) = name.compareTo(c.name)
  

}

@Entity { val name="ContactsCategory" }
class Category extends Serializable
{
  @Id @GeneratedValue @BeanProperty
  var id: java.lang.Integer = _
  
  @BeanProperty
  var name: String = _
  
  override def toString = name
}
