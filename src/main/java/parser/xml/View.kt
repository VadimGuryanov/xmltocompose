package parser.xml

import ast.Node
import ast.attributes.ViewAttributes
import ast.values.LayoutSize
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParser.START_TAG
import parser.xml.navigation.action
import parser.xml.navigation.argument
import parser.xml.navigation.fragment
import parser.xml.navigation.navigation
import parser.xml.theme.colorResource
import parser.xml.theme.resource
import parser.xml.theme.style
import parser.xml.values.constraints
import parser.xml.values.drawable
import parser.xml.values.layoutSize
import parser.xml.values.padding
import parser.xml.view.*
import parser.xml.viewgroup.*

fun XmlPullParser.node(): Node {
    require(START_TAG, null, null)

    return when (name) {
        // ViewGroupNode
        "LinearLayout" -> linearLayout()
        "FrameLayout" -> frameLayout()

        // ViewNode
        "View" -> view()
        "TextView" -> textView()
        "ImageView" -> imageView()
        "Button" -> button()
        "CheckBox" -> checkBox()
        "RadioButton" -> radioButton()
        "EditText" -> editText()
        "Switch" -> switch()

        // AndroidX
        "androidx.constraintlayout.widget.ConstraintLayout" -> constraintLayout()
        "androidx.cardview.widget.CardView",
        "com.google.android.material.card.MaterialCardView" -> cardView()
        "androidx.recyclerview.widget.RecyclerView" -> recyclerView()
        "com.google.android.material.bottomnavigation.BottomNavigationView" -> bottomNavView()
        "androidx.appcompat.widget.Toolbar" -> toolbarView()

        //Menu
        "menu" -> menu()
        "item" -> item()

        // Navigation
        "navigation" -> navigation()
        "fragment" -> fragment()
        "action" -> action()
        "argument" -> argument()

        //Theme
        "resources" -> resource()
        "style" -> style()
        "color" -> colorResource()

        else -> unknown()
    }
}

fun XmlPullParser.viewAttributes(): ViewAttributes {
    return ViewAttributes(
            id = id(),
            width = layoutSize("android:layout_width") ?: LayoutSize.WrapContent,
            height = layoutSize("android:layout_height") ?: LayoutSize.WrapContent,
            background = drawable("android:background"),
            padding = padding(),
            constraints = constraints()
    )
}

fun XmlPullParser.id(name: String = "android:id"): String? {
    val id = getAttributeValue(null, name) ?: return null

    return when {
        id.startsWith("@+id/") -> id.substring(5)
        id.startsWith("@id/") -> id.substring(4)
        else -> throw Exception("Cannot parse id: $id")
    }
}