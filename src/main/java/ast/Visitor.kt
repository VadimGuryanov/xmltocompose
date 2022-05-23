package ast

import ast.view.*
import ast.viewgroup.*

interface Visitor {
    fun visitLayout(layout: Layout)
    fun visitView(node: ViewNode)
    fun visitButton(node: ButtonNode)
    fun visitTextView(node: TextViewNode)
    fun visitEditText(node: EditTextNode)
    fun visitCardView(node: CardViewNode)
    fun visitImageView(node: ImageViewNode)
    fun visitLinearLayout(node:LinearLayoutNode)
    fun visitFrameLayout(node: FrameLayoutNode)
    fun visitCheckBox(node: CheckBoxNode)
    fun visitRadioButton(node: RadioButtonNode)
    fun visitConstraintLayout(node: ConstraintLayoutNode)
    fun visitUnknown(node: UnknownNode)
    fun visitSwitch(node: SwitchNode)
}
