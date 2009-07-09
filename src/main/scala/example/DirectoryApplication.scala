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
  
  override def newRequestCycle(request: Request, response: Response) = {
    new DataRequestCycle(this, request.asInstanceOf[WebRequest], response.asInstanceOf[WebResponse])
  }
  
  protected override def init {
    mountBookmarkablePage("/list", classOf[ListAndEdit])
  }
  
  override def configureHibernate(config: AnnotationConfiguration) = {  
    super.configureHibernate(config)
    config.addAnnotatedClass(classOf[Contact])
    config.addAnnotatedClass(classOf[Name])
    config.addAnnotatedClass(classOf[StreetAddress])
    config.addAnnotatedClass(classOf[Login])
    config.addAnnotatedClass(classOf[Role])
  }
  
}
