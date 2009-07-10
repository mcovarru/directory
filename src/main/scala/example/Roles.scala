package example

import java.io.Serializable
import javax.persistence._

import org.hibernate.annotations.ForceDiscriminator
import org.hibernate.annotations.Immutable

/* Do this in Scala since we can pile all these trivial and highly related
 * subclasses in one convenient place, plus the base class  */


@Entity
@Inheritance { val strategy = InheritanceType.SINGLE_TABLE }
@DiscriminatorColumn { val name = "role_nam", val discriminatorType = DiscriminatorType.STRING }
@ForceDiscriminator
@Table { val name="z_roles" }
abstract class Role extends Serializable with Comparable[Role] {
  
  	@Id
	@GeneratedValue
	@Column { val name="role_id", val insertable=false, val updatable=false }
	var id: java.lang.Long = _
	
	@Column { val name="role_nam", val insertable=false, val updatable=false }
	var name: String = _
 
	def compareTo(other: Role) = name.compareTo(other.name)
 
	override def equals(o: Any) = name.equals(o.asInstanceOf[Role].name)
 
	override def hashCode = name.hashCode
} 


@Entity
@DiscriminatorValue("TEST")
class TestRole extends Role;


@Entity
@DiscriminatorValue("INVSTGTR")
class InvestigatorRole extends Role;


@Entity
@DiscriminatorValue("ADMIN")
class AdminRole extends Role;


@Entity
@DiscriminatorValue("REPORT")
class ReportViewerRole extends Role;


@Entity
@DiscriminatorValue("PFGRC_USER")
class PfgrcUserRole extends Role;
