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
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pmr.sq1_rh_app.Class.ProfilListeToDo
import com.pmr.sq1_rh_app.R
import java.lang.reflect.Type


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), View.OnClickListener {

    var refBtnPseudoOk: Button? = null
    var refAreaPseudo: EditText? = null // sauvgarder le pseudo..
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "OnCreate")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* On récupère les views */
        refBtnPseudoOk = findViewById(R.id.btn_pseudo_OK) //id dans la classe R
        refAreaPseudo = findViewById(R.id.pseudoName)

        /* Listeners sur les boutons */
        /* On pourrait passer ici directement l'action mais on doit enregistrer le texte donc on préférera overrider onclick plus tard */
        refBtnPseudoOk?.setOnClickListener(this)

        /* Listeners sur le champ texte pour les log */
        refAreaPseudo?.setOnClickListener(this)

    }

    /*Implémente le clique sur le bouton Ok pour enregistrer le pseudo dans les préférences et pour
     passer à l'activité ChoixListActivity*/

    override fun onClick(v: View) {

        Log.i(TAG, "OnClick ${v.id}") // v is the clicked view

        when (v.id) {

            R.id.btn_pseudo_OK -> {

                /* Contenu champ texte */
                val s = refAreaPseudo?.text.toString()

                // On récupère les profils dans ces données  //

                var profils = getProfils()

                // On met tous les autres utilisateurs inactifs //
                profils.forEach { it.active = false }

                // Vérification de l'existence de l'utilisateur et connexion ou création puis connexion //
                if (profils.filter { it.login == s }
                        .isEmpty()) { // on crée l'utilisateur
                    // condition par laquelle 2 pseudos ne peuvent pas être égaux (pas de password donc on est restreint à cette condition

                    /* Enregistrer dans les préférences le nom du pseudo */
                    var pseudo = ProfilListeToDo(s, active = true) //création du profil
                    profils.add(pseudo)
                    editPrefs(profils, "profils")

                    Log.i(TAG, "logged_user" + s)

                } else { // on connecte l'utilisateur

                    profils.filter { it.login == s }[0].active =
                        true //on met le premier login correspondant en actif (il existe forcément et il ne peut pas y avoir 2 pseudos égaux sinon oau lieu de le créer on se seait connecté
                    editPrefs(profils, "profils")

                    Log.i(TAG, "logged_user" + s)
                }


                /* Afficher ChoixListActivity */
                val versChoixList = Intent(this@MainActivity, ChoixListActivity::class.java)
                /* On change d'activité */
                Log.i(TAG, "Starting ChoixListActivity")
                startActivity(versChoixList)



            }

            // Lors du click sur le texte, on sélectionne le champ et on préremplis avec le dernier utilisateur (donc l'utilisateur actif) //
            R.id.pseudoName -> {
                var profils = getProfils()
                var loggedInUserList: List<ProfilListeToDo> = profils.filter { it.active }
                if (loggedInUserList.size == 1) refAreaPseudo?.setText(loggedInUserList[0].login) //si on a déjà renseigné un login, c'est l'utilisateur loggé, on remplis donc le texte losr du click
                refAreaPseudo?.selectAll() //pour UX, on sélectionne le champ
            }
        }
    }


    /* Gestion des menus */

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(
            R.menu.menu,
            menu
        );
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
        //return super.onOptionsItemSelected(item)
    }

    /* Objet TAG pour pouvoir relever les traces d'exécution */

    companion object {
        private const val TAG = "TRACES_MAIN"
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
            //val profils: ProfilListeToDo = gson.fromJson<ProfilListeToDo>(json, ProfilListeToDo::class.java)
            Log.i(TAG, "pseudo_list found " )


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

