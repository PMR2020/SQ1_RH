package com.pmr.sq1_rh_app.Activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pmr.sq1_rh_app.adapter.ItemAdapter
import com.pmr.sq1_rh_app.Class.ItemToDo
import com.pmr.sq1_rh_app.Class.ProfilListeToDo
import com.pmr.sq1_rh_app.R
import java.lang.reflect.Type

@Suppress("DEPRECATION")
class ShowListActivity : AppCompatActivity(), ItemAdapter.ActionListenerItem, View.OnClickListener {
    var refBtnItemOk: Button? = null
    var refAreaItem: EditText? = null

    /* Gestion des recyclerview */
    private val adapter = newAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "OnCreate")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_list)

        /* On récupère les views */
        refBtnItemOk = findViewById(R.id.btn_item_OK) //id dans la classe R
        refAreaItem = findViewById(R.id.area_item)

        /* Listeners sur les boutons */

        refBtnItemOk?.setOnClickListener(this)

        /* Listeners sur le champ texte pour les log */
        refAreaItem?.setOnClickListener(this)


        refreshRecyclerView()


    }

    /* Création de la fonction pour pouvoir l'appeler à la suite de la création d'une nouvelle liste */
    fun refreshRecyclerView() {
        Log.i(TAG,"fonction refresh du recyclerview appelée")
        /* On trouve la Recycler view */
        val item_view: RecyclerView = findViewById(R.id.item)

        /* Affichage de toutes les listes pour le pseudo actuel */
        val dataSet = mutableListOf<ItemToDo>()

        // On recharge les donéees qui ont peut etre changé//
        // Chargement des profils //
        var profils = getProfils()

        // MAJ du profil du user en question //
        var user = profils.filter { it.active }[0]

        if (user!!.mesListToDo.size != 0) {
            Log.i(TAG, user!!.login)

            var list =
                user!!.mesListToDo.filter { it.active }[0] // on prend la première et la seule d'après notre action dans choixlistactivity avant de passer à cete activité

            for (item in list.listItemsToDo) { //si pas d'items on n'entre juste pas dans la boucle , on n'affichera donc rien
                dataSet.add(item)
            }
        }

        // Pour affichage de la recyclerview //
        item_view.adapter = adapter
        item_view.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        adapter.showData(dataSet)

    }

    fun addItemToActiveList(item: ItemToDo) {

        Log.i(TAG, "additemtoactivelist called")

        // On recharge les donées qui ont peut etre changé //
        // Chargement des profils //
        //loadPrefs()
        var profils = getProfils()

        // MAJ du profil du user en question //
        var user = profils.filter { it.active }[0]
        var list = user!!.mesListToDo.filter { it.active }[0].listItemsToDo

        for (itemliste in list) {
            if (itemliste.description == item.description) {
                Log.i(TAG, "Item non ajouté car déjà présent")
                Toast.makeText(
                    this,
                    "Item non ajouté car déjà présent, veuillez saisir une autre description",
                    Toast.LENGTH_LONG
                ).show()
                return //on sort de la fonction avant de l'ajouter
            }
        }
        list.add(
            item
        )


        editPrefs(profils, "profils")
    }

    /* Pour recyclerview */
    private fun newAdapter(): ItemAdapter {

        val adapter = ItemAdapter(
            this
        )
        return adapter
    }


    override fun onItemClicked(itemToDo: ItemToDo) {
        Log.d("CShowListActivity", "onItemClicked $itemToDo")

        // On recharge les données qui ont peut etre changé //
        // Chargement des profils //

        var profils = getProfils()

        // MAJ du profil du user en question //
        var user = profils.filter { it.active }[0]

        for (item in user!!.mesListToDo.filter { it.active }[0].listItemsToDo) {
            if (item.description == itemToDo.description) {


                item.fait = !item.fait // on change l'attribut fait de l'item
            }
        }

        editPrefs(profils, "profils")

        refreshRecyclerView() //pour afficher qu'on a coché la case

    }


    /* Gestion des champs (bouton+++)*/
    override fun onClick(v: View) {

        Log.i(TAG, "OnClick ${v.id}") // v is the clicked view

        when (v.id) {

            R.id.btn_item_OK -> {


                Log.i(TAG,"Ok button clicked")

                /* Création du nouvel item */

                var itemToDo = ItemToDo(refAreaItem?.text.toString())

                /* Enregistrer dans les préférences (donc dans les listes du user) le nom de l'item */
                addItemToActiveList(itemToDo)

                /* Afficher le résultat avec notre fonction de refresh*/
                refreshRecyclerView()

            }

            R.id.area_item -> {
                /* Log done above the when */
            }
        }
    }

    /* Gestion des menus */

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(
            R.menu.menu,
            menu
        );        // maybe change here to change the name in the menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId;
        return if (id == R.id.Preferences) {
            Log.i(TAG, "Settings menu option clicked")

            /* Creation de l'intent pour switcher d'activité */
            val intent = Intent(this, SettingsActivity::class.java)

            /* On change l'activité */
            Log.i(TAG, "Starting SettingsActivity")
            startActivity(intent)
            true
        }
        /* Code si besoin d'autres menus */
        else {
            true
        }

    }


    /* Objet TAG pour pouvoir relever les traces d'exécution */

    companion object {
        private const val TAG = "TRACES_ShowList"
    }

    // Gestion des enregistrements dans les préférences de l'app //
    fun loadPrefs(): SharedPreferences {
        Log.i(TAG,"Récupération des données de l'application")
        // récupération des données //
        var prefs = PreferenceManager.getDefaultSharedPreferences(this)
        return prefs
    }

    fun getProfils(): MutableList<ProfilListeToDo> {
        Log.i(TAG,"Récupération des profils")
        // Récupération des profils //
        val gson = Gson()
        val json: String? = loadPrefs().getString("profils", "") //lecture de la valeur

        var profils = mutableListOf<ProfilListeToDo>()

        if (json != "") {

            /* Solution pour enregistrer une liste de pseudos en json */

            val collectionType: Type = object :
                TypeToken<MutableList<ProfilListeToDo>>() {}.type
            profils = gson.fromJson(json, collectionType)

            Log.i(TAG, "pseudo_list found : " + json)


        } else {
            Log.i(TAG, "pseudo_list NOT found")
        }
        return profils //vide si non trouvé

    }

    fun editPrefs(
        item: MutableList<ProfilListeToDo>,
        name: String
    ) {
        Log.i(TAG,"Enregistrement des profils dans les préférences de l'applicaiton")
        val gson_set = Gson()
        val json_set: String = gson_set.toJson(item) //écriture de la valeur

        var prefs = loadPrefs() //on charge les dernières préférences
        var editor = prefs.edit()
        editor?.putString(name, json_set)
        editor?.apply()
    }


}