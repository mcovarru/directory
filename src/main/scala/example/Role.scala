package example

import java.io.Serializable
import javax.persistence._

@Entity 
@Table { val name="z_roles" }
class Role extends Serializable with Comparable[Role]
{

  @Id
  @GeneratedValue
  @Column { val name="role_id" }
  var id: java.lang.Integer = _

  @Column { val name="role_nam" }
  var name: String = _

  def compareTo(other: Role) = name.compareTo(other.name)

  override def equals(oth: Any) = { name == oth.asInstanceOf[Role].name }

  override def hashCode = name.hashCode
  
}

