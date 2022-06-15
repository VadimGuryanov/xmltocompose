package ast

import ast.navigation.ActionNode
import ast.navigation.ArgumentNode
import ast.navigation.FragmentNode
import ast.navigation.NavigationNode
import ast.theme.ColorNode
import ast.theme.StyleNode
import ast.theme.ResourceNode
import ast.view.ItemNode
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
    fun visitRecyclerView(node: RecyclerNode)
    fun visitBottomNavView(node: BottomNavNode)
    fun visitToolbarView(node: ToolbarNode)
    fun visitMenuItem(node: ItemNode)
    fun visitMenu(node: MenuNode)

    fun visitNavigate(node: NavigationNode)
    fun visitFragment(node: FragmentNode)
    fun visitAction(node: ActionNode)
    fun visitArguments(node: ArgumentNode)

    fun visitTheme(light: Layout, night: Layout)
    fun visitResource(node: ResourceNode)
    fun visitStyle(node: StyleNode)
    fun visitColor(node: ColorNode)
    fun visitStyleItem(node: ItemForStyleNode)
}
