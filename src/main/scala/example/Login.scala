package example

import java.io.Serializable
import javax.persistence._

@Entity 
@Table { val name="z_logins" }
class Login extends Serializable with Comparable[Login]
{

  @Id
  @GeneratedValue
  @Column { val name="login_id" }
  var id: java.lang.Integer = _

  @Column { val name="login_nam" }
  var name: String = _

  @Column { val name="login_pwd" }
  var password: String = _

  var email: String = _

  def compareTo(other: Login) = name.compareTo(other.name)

  override def hashCode = email.hashCode

  override def equals(oth: Any) = { email == oth.asInstanceOf[Login].email }


}
