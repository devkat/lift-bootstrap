package net.devkat.lift.http

import net.liftweb.http._
import scala.xml._
import net.liftweb.util._
import net.liftweb.util.Helpers._
import se.fishtank.css.selectors.Selectors._
import net.liftweb.common._
import net.liftweb.http.S._

trait BootstrapScreen extends LiftScreen {
  
  override def cancelButton: Elem =
    <button class="btn">{S.?("Cancel")}</button>

  override def finishButton: Elem =
    <button class="btn btn-primary">{S.?("Finish")}</button>

  def elem(n:NodeSeq) = n(0).asInstanceOf[Elem]
  
  val postProcess: NodeSeq => NodeSeq =
    ".form-group" #> { n:NodeSeq =>
      val e = elem(n)
      e $ """span[class~="has-error"]""" match {
        case Nil => <div class="form-group">{e \ "_"}</div>
        case _ => <div class="form-group has-error">{e \ "_"}</div>
      }
    } andThen
    "span" #> { n:NodeSeq =>
      val e = elem(n)
      println("checking radio " + e + " -> " + (e $ """input[type="radio"]"""))
      e $ """input[type="radio"]""" match {
        case Nil => e
        case l => <div class="radio"><label>{l}{e.descendant.filter(_.isAtom)}</label></div>
      }
    } andThen
    ".label [class]" #> ""
  
  implicit def toRadioOptions(s:Seq[Pair[String, String]]) = s.map{case (k, l) => RadioOption(k, l)}
    
  def typedRadio(name: => String, default: => String, choices: => Seq[Pair[String, String]], stuff: FilterOrValidate[String]*):
      Field {type ValueType = String; type OtherValueType = Seq[RadioOption]} = {
       
    val eAttr = grabParams(stuff)
    makeField[String, Seq[RadioOption]](name, default,
      field =>
        Full(BootstrapSHtml.typedRadio(field.otherValue,
          Full(field.is),
          field.set _,
          eAttr: _*).toForm),
      OtherValueInitializerImpl[Seq[RadioOption]](() => choices),
      stuff: _*)
  }
    
  override def renderHtml: NodeSeq = postProcess(super.renderHtml)
  
}

trait CssBoundBootstrapScreen extends CssBoundLiftScreen with BootstrapScreen {
  
  override def labelSuffix: NodeSeq = NodeSeq.Empty
  
  override def defaultToAjax_? : Boolean = true
  
  override def allTemplate = savedDefaultXml

  protected def defaultAllTemplate = super.allTemplate
     
  override def defaultFieldNodeSeq: NodeSeq =
    <div class="form-group">
      <label class="label"></label>
      <span class="form-control value"></span>
      <span class="help-block help"></span>
      <span class="help-block has-error errors">
        <span class="error"></span>
      </span>
    </div>
}

case class RadioOption(value: String, label: String) {
  override def toString() = label
}

object BootstrapSHtml extends SHtml {
  
  def typedRadio(opts: Seq[RadioOption], deflt: Box[String], func: String => Any,
      attrs: SHtml.ElemAttr*): ChoiceHolder[RadioOption] =
    typedRadio_*(opts, deflt, SFuncHolder(func), attrs: _*)
    
  def typedRadio_*(opts: Seq[RadioOption], deflt: Box[String],
      func: AFuncHolder, attrs: SHtml.ElemAttr*): ChoiceHolder[RadioOption] = {
    fmapFunc(func) { name => {
      val itemList = opts map { case key => ChoiceItem(key,
        attrs.foldLeft(
          <input type="radio" name={name} value={key.value}/>
        )(_ % _) % checked(deflt.filter((s: String) => s == key.value).isDefined))
      }
      ChoiceHolder(itemList)
    }}
  }

  def checked(in: Boolean) = if (in) new UnprefixedAttribute("checked", "checked", Null) else Null
  
}