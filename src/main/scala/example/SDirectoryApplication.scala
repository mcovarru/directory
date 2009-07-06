package example

import net.databinder.hib._
import net.databinder.hib.conv.DataConversationRequestCycle

import org.apache.wicket._
import org.apache.wicket.protocol.http._

import org.hibernate.Session
import org.hibernate.cfg.AnnotationConfiguration

import org.slf4j._


object SDirectoryApplication
{
  val logger = LoggerFactory.getLogger(classOf[DirectoryApplication])  
}


class SDirectoryApplication extends DataApplication
{
  
  def getHomePage = classOf[ListAndEdit]
  
  override def newRequestCycle(request: Request, response: Response) = {
    new DataConversationRequestCycle(this, request.asInstanceOf[WebRequest], response.asInstanceOf[WebResponse])
  }
  
  protected override def init {
    if (isDevelopment()) {
      Databinder.ensureSession(new SessionUnit() {
        override def run(sess: Session) = {
          val categories = 
            sess.createQuery("select count(*) from ContactsCategory").uniqueResult().asInstanceOf[Long]
          if (categories == 0) {
            SDirectoryApplication.logger.info("No categories found in development mode.")
            val names = Array("Friend", "Roman", "Countryman")
            for (name <- names) {
              val cat = new Category()
              cat.setName(name)
              sess.save(cat)
            }
            sess.getTransaction().commit()
            SDirectoryApplication.logger.info("Created default categories.")
          }
          // need to return null for some reason?
          null
        }
      })
    }

    mountBookmarkablePage("/list", classOf[ListAndEdit])

  }
  
  override def configureHibernate(config: AnnotationConfiguration) = {  
    super.configureHibernate(config)
    config.addAnnotatedClass(classOf[Contact])
    config.addAnnotatedClass(classOf[Category])
  }
  
}