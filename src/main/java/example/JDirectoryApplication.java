package example;

import net.databinder.hib.DataApplication;
import net.databinder.hib.Databinder;
import net.databinder.hib.SessionUnit;
import net.databinder.hib.conv.DataConversationRequestCycle;


import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.hibernate.Session;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple app for listing and editing phone numbers.
 */
public class JDirectoryApplication extends DataApplication {
	Logger logger = LoggerFactory.getLogger(JDirectoryApplication.class);
	
	/** Check/create categories, set up nice URL.  */
	protected void init() {
		// create some categories for development if none exist
		if (isDevelopment())
			// during init there is no RequestCycle-based Hibernate session
			Databinder.ensureSession(new SessionUnit() {
				public Object run(Session sess) {
					long categories = (Long)
						sess.createQuery("select count(*) from ContactsCategory")
						.uniqueResult();
					if (categories == 0) {
						logger.info("No categories found in development mode.");
						String[] names = new String[] {"Friend", "Roman", "Countryman"};
						for (String name : names) {
							Category cat = new Category();
							cat.setName(name);
							sess.save(cat);
						}
						sess.getTransaction().commit();
						logger.info("Created default categories.");
					}
					return null;
				}
			});
		mountBookmarkablePage("/list", JListAndEdit.class);
	}
	
	@Override
	public RequestCycle newRequestCycle(Request request, Response response) {
		return new DataConversationRequestCycle(this, (WebRequest) request, (WebResponse) response);
	}
	
	/** Go to ListAndEdit(the only page) if no page requested. */
	public Class getHomePage() {
		return JListAndEdit.class;
	}
	
	/** Set default config and map to Contact table. */
	protected void configureHibernate(AnnotationConfiguration config) {
		super.configureHibernate(config);
		config.addAnnotatedClass(Contact.class);
		config.addAnnotatedClass(Category.class);
	}
}
