package example

import java.util._

import net.databinder.components._
import net.databinder.components.hib._
import net.databinder.hib.Databinder
import net.databinder.hib.conv.components.ConversationPage
import net.databinder.models.hib._

import org.apache.wicket.AttributeModifier
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior
import org.apache.wicket.ajax.markup.html.AjaxLink
import org.apache.wicket.ajax.markup.html.form.AjaxButton
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow
import org.apache.wicket.markup.ComponentTag
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.form._
import org.apache.wicket.markup.html.list._
import org.apache.wicket.markup.html.panel._
import org.apache.wicket.model.IModel
import org.apache.wicket.model.Model
import org.hibernate.Query
import org.hibernate.Session


class ListAndEdit extends ConversationPage {
  var form: DataForm = _
  var modalEdit: ModalWindow = _
  var categoryFilter: DropDownChoice = _
  var contactsWrap: AjaxCell = _
  val categoryListModel = new HibernateListModel("from ContactsCategory order by Name")
  val unflushedSaves = new HashSet[Contact]
  
  add(new DataStyleLink("css"))
  add(new FeedbackPanel("feedback"))
  
  val search = new SearchPanel("search") {
    override def onUpdate(target: AjaxRequestTarget) {
      target.addComponent(contactsWrap)
    }
  }

  add(search)
  
  categoryFilter = new DropDownChoice("categoryFilter",
                                      new HibernateObjectModel(), categoryListModel)
  add(categoryFilter)

  categoryFilter.add(new AjaxFormComponentUpdatingBehavior("onChange") {
    override protected def onUpdate(target: AjaxRequestTarget) {
      target.addComponent(contactsWrap)
    }
  })
  categoryFilter.setNullValid(true)

  modalEdit = new ModalWindow("modalEdit")
  modalEdit.setContent(new EditPanel(modalEdit.getContentId()))
  modalEdit.setInitialWidth(350)
  modalEdit.setInitialHeight(230)
  add(modalEdit)

  val contactList = new HibernateListModel(getString("query"),
                                           new QueryBinder() {
                                             override def bind(query: Query) {
                                               query
                                               .setString("search", search.getSearch())
                                               .setParameter("category", categoryFilter.getModelObject())
                                             }
                                           }) {
                                             override protected def load() = {
                                               val contacts = super.load().asInstanceOf[List[Contact]]
                                               if (contacts.addAll(unflushedSaves))
                                                 Collections.sort(contacts)
                                               contacts
                                             }
                                           }

  contactsWrap = new AjaxCell("contactsWrap")
  add(contactsWrap)

  contactsWrap.add(new PropertyListView("contacts", contactList) {
    override def populateItem(item: ListItem) {
      val existing = Databinder.getHibernateSession().contains(item.getModelObject())
      var link: WebMarkupContainer = null
      if (existing) {
        link = new AjaxLink("link") {
          override def onClick(target: AjaxRequestTarget) {
            form.setPersistentObject(item.getModelObject())
            modalEdit.show(target)
          }
        }
        if (unflushedSaves.contains(item.getModelObject())) {
          link.add(new AttributeModifier("class", true, new Model("unflushed")))
        }
      }
        else {
          link = new WebMarkupContainer("link") {
            protected override def onComponentTag(tag: ComponentTag) {
              tag.setName("span")
            }
          }
          link.add(new AttributeModifier("class", true, new Model("deleted")))
        }
      item.add(link.add(new Label("name")))

      item.add(new Label("phoneNumber"))
      item.add(new Label("category"))
      item.add(new AlternatingClassModifier(item)) // table row color

    }
  })

  add(new AjaxLink("new") {
    override def onClick(target: AjaxRequestTarget) {
      form.clearPersistentObject()
      modalEdit.show(target)
    }
  })

  val saveForm = new DataFormBase("saveForm") {
    protected override def onSubmit() {
      super.onSubmit()	// flush changes
      unflushedSaves.clear()
    }
  }

  add(saveForm)

  saveForm.add(new Button("revert") {
    override def onSubmit() {
      Databinder.getHibernateSession().clear()
      unflushedSaves.clear()
    }
  }.setDefaultFormProcessing(false))



  class EditPanel(id: String) extends Panel(id) {

    var feedback: FeedbackPanel = _

    form = new EditForm("editForm")
    add(form)

    feedback = new FeedbackPanel("feedback")
    add(feedback)
    feedback.setOutputMarkupId(true)

    class EditForm(id: String) extends DataForm(id, new HibernateObjectModel(classOf[Contact])) {

      var editHeader = new WebMarkupContainer("editHeader") {
        override def isVisible = getContact().id != null 
      }
      add(editHeader.add(new Label("name")))
      
      add(new WebMarkupContainer("newHeader") {
        override def isVisible = getContact().id == null
      })
      
      add(new RequiredTextField("name").setLabel(new Model("contact name")))
      add(new TextField("phoneNumber"))

      var category = new DropDownChoice("category", categoryListModel)

      category.setNullValid(true)
      add(category)

      add(new AjaxButton("okay", this) {
        protected override def onSubmit(target: AjaxRequestTarget, form: Form) {
          modalEdit.close(target)
          target.addComponent(contactsWrap)
        }
        protected override def onError(target: AjaxRequestTarget, form: Form) {
          target.addComponent(feedback)
        }
      })

      add(new AjaxLink("cancel") {
        override def onClick(target: AjaxRequestTarget) {
          modalEdit.close(target)
          target.addComponent(contactsWrap)
        }
      })

      add(new AjaxLink("delete") {
        override def onClick(target: AjaxRequestTarget) {
          var sess = Databinder.getHibernateSession()
          sess.delete(getContact())
          clearPersistentObject()
          modalEdit.close(target)
          target.addComponent(contactsWrap)
        }
      })
      
      protected def getContact() = getModelObject().asInstanceOf[Contact]

      protected override def onSubmit = {
        var contact = getContact
        var session = Databinder.getHibernateSession()

        if (!session.contains(contact)) {
          session.save(contact)
          unflushedSaves.add(contact)
        }
        clearPersistentObject()
      }

    }
    
  }
  
}

