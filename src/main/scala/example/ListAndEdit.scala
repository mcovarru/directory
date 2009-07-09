package example

import java.util._

import net.databinder.components._
import net.databinder.components.hib._
import net.databinder.hib.Databinder
import net.databinder.models.hib._

import org.apache.wicket.AttributeModifier
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior
import org.apache.wicket.ajax.markup.html.AjaxLink
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow
import org.apache.wicket.markup.ComponentTag
import org.apache.wicket.markup.html._
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.form._
import org.apache.wicket.markup.html.list._
import org.apache.wicket.markup.html.panel._
import org.apache.wicket.model._
import org.hibernate.Query
import org.hibernate.Session


class ListAndEdit extends WebPage {
  var form: DataForm = _
  var modalEdit: ModalWindow = _
  var contactsWrap: AjaxCell = _
  
  add(new DataStyleLink("css"))
  add(new FeedbackPanel("feedback"))
  
  val search = new SearchPanel("search") {
    override def onUpdate(target: AjaxRequestTarget) {
      target.addComponent(contactsWrap)
    }
  }

  add(search)
  

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
                                             }
                                           }) 

  contactsWrap = new AjaxCell("contactsWrap")
  add(contactsWrap)

  contactsWrap.add(new PropertyListView("contacts", contactList) {
    override def populateItem(item: ListItem) {
      var link: WebMarkupContainer = null
      link = new AjaxLink("link") {
        override def onClick(target: AjaxRequestTarget) {
          form.setPersistentObject(item.getModelObject())
          modalEdit.show(target)
        }
      }

      val mobj = item.getModelObject()
      item.add(link.add(new Label("name")))

      item.add(new Label("phone"))

      // this really needs to become a hyperlinked mailto: as soon as I figure out how
      item.add(new Label("email"))

      val addr = item.getModelObject().asInstanceOf[Contact].streetAddress

      item.add(new Label("addr_line_1", new PropertyModel(addr, "first")))
      item.add(new Label("addr_line_2", new PropertyModel(addr, "second")))
      item.add(new Label("addr_line_3", new PropertyModel(addr, "third")))
      
      item.add(new Label("city", new PropertyModel(addr, "city")))
      item.add(new Label("state", new PropertyModel(addr, "state")))
      item.add(new Label("postalCode", new PropertyModel(addr, "postalCode")))
      item.add(new Label("country", new PropertyModel(addr, "country")))

      item.add(new AlternatingClassModifier(item)) // table row color

    }
  })



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
      add(new TextField("phone"))

      add(new AjaxLink("okay") {
        override def onClick(target: AjaxRequestTarget) {
          modalEdit.close(target)
          target.addComponent(contactsWrap)
        }
      })

      protected def getContact() = getModelObject().asInstanceOf[Contact]

    }
    
  }
  
}

