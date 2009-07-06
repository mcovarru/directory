package example;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.databinder.components.AlternatingClassModifier;
import net.databinder.components.DataStyleLink;
import net.databinder.components.AjaxCell;
import net.databinder.components.hib.DataForm;
import net.databinder.components.hib.DataFormBase;
import net.databinder.components.hib.SearchPanel;
import net.databinder.hib.Databinder;
import net.databinder.hib.conv.components.ConversationPage;
import net.databinder.models.hib.HibernateListModel;
import net.databinder.models.hib.HibernateObjectModel;
import net.databinder.models.hib.QueryBinder;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * Single page for listing and editing names and phone numbers.
 */
public class ListAndEdit extends ConversationPage {
	private DataForm form;
	private ModalWindow modalEdit;
	private DropDownChoice categoryFilter;
	private AjaxCell contactsWrap;
	private HibernateListModel categoryListModel =
		new HibernateListModel("from ContactsCategory order by Name");
	/** Contacts that have been saved in the session but not flushed */
	private Set<Contact> unflushedSaves = new HashSet<Contact>();
			
	public ListAndEdit() {
		
		add(new DataStyleLink("css"));

		add(new FeedbackPanel("feedback"));

		// ready-made panel for a live AJAX search 
		final SearchPanel search = new SearchPanel("search") {
			public void onUpdate(AjaxRequestTarget target) {
				target.addComponent(contactsWrap);
			}
		};
		add(search);
		
		// dropdown filter to limit the listing to selected categories
		add(categoryFilter = new DropDownChoice("categoryFilter", 
				new HibernateObjectModel(), categoryListModel));
		
		categoryFilter.add(new AjaxFormComponentUpdatingBehavior("onChange" ) {
			protected void onUpdate(AjaxRequestTarget target) {
				target.addComponent(contactsWrap);
			}
		});
		categoryFilter.setNullValid(true);	// null option display value set in properties
		
		modalEdit = new ModalWindow("modalEdit");
		modalEdit.setContent(new EditPanel(modalEdit.getContentId()));
		modalEdit.setInitialWidth(350);
		modalEdit.setInitialHeight(230);
		add(modalEdit);
		
		// get list of contacts all contacts (see query in properties file)
		final IModel contactList = new HibernateListModel(getString("query"),  
				new QueryBinder() {
			/** Uses default search binder and custom category filter */
			public void bind(Query query) {
				query
					.setString("search", search.getSearch())
					.setParameter("category", categoryFilter.getModelObject());
			}
		}) {
			/** @return results of query with unflushed contacts added */
			@SuppressWarnings("unchecked")
			protected Object load() {
				List contacts = (List) super.load();	// contacts from DB query
				if (contacts.addAll(unflushedSaves))
					Collections.sort(contacts);
				return contacts;
			}
		};
		add(contactsWrap = new AjaxCell("contactsWrap"));
		contactsWrap.add(new PropertyListView("contacts", contactList)  
		{
			protected void populateItem(final ListItem item) {
				boolean existing = Databinder.getHibernateSession().contains(item.getModelObject());
				WebMarkupContainer link;
				if (existing) {
					link = new AjaxLink("link") {
						public void onClick(AjaxRequestTarget target) {
							form.setPersistentObject(item.getModelObject());
							modalEdit.show(target);
						}
					};
					if (unflushedSaves.contains(item.getModelObject()))
						// use special appearance as these aren't subject to the filters
						link.add(new AttributeModifier("class", true, new Model("unflushed")));
				} else {
					link = new WebMarkupContainer("link") {
						protected void onComponentTag(ComponentTag tag) {
							tag.setName("span");
						}
					};
					link.add(new AttributeModifier("class", true, new Model("deleted")));
				}
				item.add(link.add(new Label("name")));

				item.add(new Label("phoneNumber"));
				item.add(new Label("category"));
				item.add(new AlternatingClassModifier(item)); // table row color
			}
		});
		
		add(new AjaxLink("new") {
			public void onClick(AjaxRequestTarget target) {
				form.clearPersistentObject();
				modalEdit.show(target);
			}
		});
		
		// form containing save (default action) and revert buttons
		Form saveForm = new DataFormBase("saveForm") {
			protected void onSubmit() {
				super.onSubmit();	// flush changes
				unflushedSaves.clear();
			}
		};
		add(saveForm);
		saveForm.add(new Button("revert") {
			public void onSubmit() {
				Databinder.getHibernateSession().clear();
				unflushedSaves.clear();
			}
		}.setDefaultFormProcessing(false));
	}
	
	public class EditPanel extends Panel {
		private FeedbackPanel feedback;
		
		public EditPanel(String id) {
			super(id);
			add(form = new EditForm("editForm"));
			add(feedback = new FeedbackPanel("feedback"));
			feedback.setOutputMarkupId(true);
		}

		/** Edit a selected or new Contact. */
		public class EditForm extends DataForm {
			public EditForm(String id) {
				super(id, new HibernateObjectModel(Contact.class));
				
				// use one of two headers depending on ID state
				WebMarkupContainer editHeader = new WebMarkupContainer("editHeader") {
					public boolean isVisible() {
						return getContact().getId() != null;
					}
				};
				add(editHeader.add(new Label("name")));
				add(new WebMarkupContainer("newHeader") {
					public boolean isVisible() {
						return getContact().getId() == null;
					}
				});
				
				// label appears in failed validation message
				add(new RequiredTextField("name").setLabel(new Model("contact name")));
				add(new TextField("phoneNumber"));
				
				DropDownChoice category = new DropDownChoice("category", categoryListModel);
				category.setNullValid(true);
				add(category);
				
				add(new AjaxButton("okay", this) {
					protected void onSubmit(AjaxRequestTarget target, Form form) {
						modalEdit.close(target);
						target.addComponent(contactsWrap);
					}
					@Override
					protected void onError(AjaxRequestTarget target, Form form) {
						target.addComponent(feedback);
					}
				});
				
				add(new AjaxLink("cancel") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						modalEdit.close(target);
						target.addComponent(contactsWrap);
					}	
				});
				
				add(new AjaxLink("delete"){
					/** Perform Contact delete. */
					public void onClick(AjaxRequestTarget target) {
						Session sess = Databinder.getHibernateSession();
						sess.delete(getContact());
						clearPersistentObject();
						modalEdit.close(target);
						target.addComponent(contactsWrap);
					}
				});
			}
			/** Convenience, retrieve and cast our model. */
			protected Contact getContact() {
				return (Contact) getModelObject();
			}
			/** Doesn't flush session, so changes may be reverted later. */
		protected void onSubmit() {
			Contact contact = getContact();
			Session session = Databinder.getHibernateSession();
			if (!session.contains(contact)) {
				session.save(contact);
				unflushedSaves.add(contact);
			}
			clearPersistentObject();
		}
	}
	}
}
