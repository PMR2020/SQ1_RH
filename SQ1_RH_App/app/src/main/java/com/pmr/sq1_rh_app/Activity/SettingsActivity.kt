@file:Suppress("DEPRECATION")

package com.pmr.sq1_rh_app.Activity

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pmr.sq1_rh_app.Class.ProfilListeToDo
import com.pmr.sq1_rh_app.R
import java.lang.reflect.Type

@Suppress("DEPRECATION")
class SettingsActivity : AppCompatActivity() {

    var ref_txt_pseudo:TextView? = null;


   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        ref_txt_pseudo = findViewById(R.id.AfichePseudo)

        // Récupération du pseudo //
        var profils = getProfils()
        if (profils.isEmpty()){
            ref_txt_pseudo?.text = "Pas d'utilisateur en ce moment"
        }
        else {
            var user =
                getProfils().filter { it.active }[0] // seul utilisatur active..
            ref_txt_pseudo?.text = user.login
        }

    }
    companion object {
        private const val TAG = "TRACES_SETTINGS"
    }

    // Gestion des enregistrements dans les préférences de l'app //
    fun loadPrefs(): SharedPreferences {
        Log.i(TAG,"Récupération des pseudos ")
        // récupération  des pseudos //
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

            /* sauvgarder la liste de pseudo en utilisant json */

            val collectionType: Type = object :
                TypeToken<MutableList<ProfilListeToDo>>() {}.type
            profils = gson.fromJson(json, collectionType)

            Log.i(TAG, "pseudo_list found : " + json)


        } else {
            Log.i(TAG, "pseudo_list n'exist pas !")
        }
        return profils //vide si non trouvé

    }

}
