package com.pmr.sq1_rh_app.Class

class ListeToDo(var titreListeToDo : String,var active:Boolean=false) {
    var listItemsToDo : MutableList<ItemToDo> = mutableListOf()
}
