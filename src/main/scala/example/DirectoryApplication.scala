package example

import net.databinder.hib._

import org.apache.wicket._
import org.apache.wicket.protocol.http._

import org.hibernate.Session
import org.hibernate.cfg.AnnotationConfiguration

import org.slf4j._


object DirectoryApplication
{
  val logger = LoggerFactory.getLogger(classOf[DirectoryApplication])  
}


class DirectoryApplication extends DataApplication
{
  
  def getHomePage = classOf[ListAndEdit]
  
  protected override def init {
    mountBookmarkablePage("/list", classOf[ListAndEdit])
  }
  
  override def configureHibernate(config: AnnotationConfiguration) = {  
    super.configureHibernate(config)
    config.addAnnotatedClass(classOf[Investigator])
    config.addAnnotatedClass(classOf[Name])
    config.addAnnotatedClass(classOf[StreetAddress])
    config.addAnnotatedClass(classOf[Login])
    config.addAnnotatedClass(classOf[Role])
    
    // trying to turn off hbm2ddl.auto in hibernate.properties does not appear
    // to have any effect in development mode.  *very* annoying!
    config.setProperty("hibernate.hbm2ddl.auto", "false")
  }
  
}
