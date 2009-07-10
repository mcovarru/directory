package example

import javax.persistence._

/* Do this in Scala since we can pile all these trivial and highly related
 * classes in one convenient place */

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
