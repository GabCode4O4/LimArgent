package com.example.limargent.Data.Movements

import android.content.Context
import com.example.limargent.Data.User.UserProvider
import com.example.limargent.Domain.MoneyMovement
import com.example.limargent.Domain.MovType
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class MovementsProvider @Inject constructor(
    //private val userProvider: UserProvider
    @ApplicationContext private val context: Context
) {
    // TODO: Gérer l'utilisateur courant (session)
    private val currentUserIdentifiant = "default" // Pour l'instant, on utilise un utilisateur par défaut

    private val fileName = "movements.json"
    private val movements = mutableListOf<MoneyMovement>()

    init{
        loadMovements()
    }

    private fun loadMovements() {
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            try {
                val jsonString = file.readText()
                val jsonArray = JSONArray(jsonString)
                movements.clear()
                for (i in 0 until jsonArray.length()) {
                    val jsonObj = jsonArray.getJSONObject(i)
                    val movement = MoneyMovement(
                        titre = jsonObj.getString("titre"),
                        montant = BigDecimal(jsonObj.getString("montant")),
                        categorie = jsonObj.getString("categorie"),
                        date = LocalDate.parse(jsonObj.getString("date")),
                        type = MovType.valueOf(jsonObj.getString("type"))
                    )
                    movements.add(movement)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                defaultMovements()
            }
        } else {
            defaultMovements()
        }
    }

    private fun defaultMovements() {
        movements.clear()

        movements.addAll(
            listOf(
                MoneyMovement(
                    titre = "Salaire Février",
                    montant = BigDecimal("2500.00"),
                    categorie = "Salaire",
                    date = LocalDate.of(2026, 2, 1),
                    type = MovType.entree
                ),
                MoneyMovement(
                    titre = "Courses Supermarché",
                    montant = BigDecimal("120.50"),
                    categorie = "Alimentation",
                    date = LocalDate.of(2026, 2, 3),
                    type = MovType.sortie
                ),
                MoneyMovement(
                    titre = "Loyer Appartement",
                    montant = BigDecimal("800.00"),
                    categorie = "Logement",
                    date = LocalDate.of(2026, 2, 5),
                    type = MovType.sortie
                ),
                MoneyMovement(
                    titre = "Essence voiture",
                    montant = BigDecimal("60.00"),
                    categorie = "Transports",
                    date = LocalDate.of(2026, 2, 6),
                    type = MovType.sortie
                ),
                MoneyMovement(
                    titre = "Sortie cinéma",
                    montant = BigDecimal("25.00"),
                    categorie = "Loisirs",
                    date = LocalDate.of(2026, 2, 7),
                    type = MovType.sortie
                ),
                MoneyMovement(
                    titre = "Cadeau anniversaire",
                    montant = BigDecimal("50.00"),
                    categorie = "Autre",
                    date = LocalDate.of(2026, 2, 10),
                    type = MovType.sortie
                )
            )
        )
        saveMovements()
    }
    fun saveMovements() {
        try {
            val jsonArray = JSONArray()
            movements.forEach { mouvement ->
                val jsonObj = JSONObject()
                jsonObj.put("titre", mouvement.titre)
                jsonObj.put("montant", mouvement.montant.toPlainString())
                jsonObj.put("categorie", mouvement.categorie)
                jsonObj.put("date", mouvement.date.toString())
                jsonObj.put("type", mouvement.type.name)
                jsonArray.put(jsonObj)
            }

            val file = File(context.filesDir, fileName)
            file.writeText(jsonArray.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getMouvements(): List<MoneyMovement> {
        //val user = userProvider.getUser(currentUserIdentifiant)
        //return user?.mouvements ?: emptyList()
        return movements.toList()
    }

    /*fun addMouvement(mouvement: MoneyMovement) {
        val user = userProvider.getUser(currentUserIdentifiant)
        if (user != null) {
            user.mouvements.add(mouvement)
            userProvider.saveUser(user)
        }
    }

    fun deleteMouvement(mouvement: MoneyMovement) {
        val user = userProvider.getUser(currentUserIdentifiant)
        if (user != null) {
            user.mouvements.remove(mouvement)
            userProvider.saveUser(user)
        }
    }*/

    fun addMouvement(m: MoneyMovement) {
        movements.add(m)
        saveMovements()
    }

    fun deleteMouvement(m: MoneyMovement) {
        movements.remove(m)
        saveMovements()
    }
}