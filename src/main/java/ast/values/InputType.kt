package ast.values

sealed class InputType {
    object Text : InputType()
    object Number : InputType()
    object Phone : InputType()
    object Uri : InputType()
    object Email : InputType()
    object Password : InputType()
    object NumberPassword : InputType()
}
