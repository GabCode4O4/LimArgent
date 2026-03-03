package com.example.limargent.Data.User

import android.content.Context
import com.example.limargent.Domain.MoneyMovement
import com.example.limargent.Domain.MovType
import com.example.limargent.Domain.User
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fileName = "users.json"
    private val users = mutableListOf<User>()

    init {
        loadUsers()
    }

    fun getUsers(): List<User> {
        return users.toList()
    }

    fun getUser(): User? {
        return users.firstOrNull()
    }

    fun getUser(identifiant: String): User? {
        return users.find { it.identifiant == identifiant }
    }

    fun saveUser(user: User) {
        val existingUser = users.find { it.identifiant == user.identifiant }
        if (existingUser != null) {
            users.remove(existingUser)
        }
        users.add(user)
        saveToFile()
    }

    fun updateUser(user: User) {
        val index = users.indexOfFirst { it.identifiant == user.identifiant }
        if (index != -1) {
            users[index] = user
            saveToFile()
        }
    }

    fun deleteUser(identifiant: String) {
        users.removeIf { it.identifiant == identifiant }
        saveToFile()
    }

    private fun loadUsers() {
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            try {
                val jsonString = file.readText()
                val jsonArray = JSONArray(jsonString)
                users.clear()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val identifiant = jsonObject.getString("identifiant")
                    val motDePasse = jsonObject.getString("motDePasse")

                    val categories = mutableListOf<String>()
                    if (jsonObject.has("categories")) {
                        val categoriesArray = jsonObject.getJSONArray("categories")
                        for (j in 0 until categoriesArray.length()) {
                            categories.add(categoriesArray.getString(j))
                        }
                    }

                    val preferences = mutableListOf<String>()
                    if (jsonObject.has("preference")) {
                        val preferencesArray = jsonObject.getJSONArray("preference")
                        for (j in 0 until preferencesArray.length()) {
                            preferences.add(preferencesArray.getString(j))
                        }
                    }

                    val mouvements = mutableListOf<MoneyMovement>()
                    if (jsonObject.has("mouvements")) {
                        val mouvementsArray = jsonObject.getJSONArray("mouvements")
                        for (k in 0 until mouvementsArray.length()) {
                            val mouvObj = mouvementsArray.getJSONObject(k)
                            val titre = mouvObj.getString("titre")
                            val montant = BigDecimal(mouvObj.getString("montant"))
                            val categorie = mouvObj.getString("categorie")
                            val dateString = mouvObj.getString("date")
                            val date = LocalDate.parse(dateString)
                            val type = MovType.valueOf(mouvObj.getString("type"))
                            mouvements.add(MoneyMovement(titre, montant, categorie, date, type))
                        }
                    }

                    users.add(User(identifiant, motDePasse, mouvements, categories, preferences))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (users.isEmpty()) {
            createDefaultUser()
        }
    }

    private fun createDefaultUser() {
        val defaultCategories = mutableListOf(
            "Alimentation", "Logement", "Transports", "Santé", "Loisirs", "Épargne", "Salaire", "Autre"
        )
        val defaultMovements = mutableListOf(
            MoneyMovement("Salaire Février", BigDecimal("2500.00"), "Salaire", LocalDate.of(2026, 2, 1), MovType.entree),
            MoneyMovement("Courses Supermarché", BigDecimal("120.50"), "Alimentation", LocalDate.of(2026, 2, 3), MovType.sortie),
            MoneyMovement("Loyer Appartement", BigDecimal("800.00"), "Logement", LocalDate.of(2026, 2, 5), MovType.sortie)
        )
        val defaultUser = User("default", "password123", defaultMovements, defaultCategories, mutableListOf("theme:light"))
        users.add(defaultUser)
        saveToFile()
    }

    private fun saveToFile() {
        try {
            val jsonArray = JSONArray()
            for (user in users) {
                val jsonObject = JSONObject()
                jsonObject.put("identifiant", user.identifiant)
                jsonObject.put("motDePasse", user.motDePasse)

                val categoriesArray = JSONArray(user.categories)
                jsonObject.put("categories", categoriesArray)

                val preferencesArray = JSONArray(user.preference)
                jsonObject.put("preference", preferencesArray)

                val mouvementsArray = JSONArray()
                for (mouv in user.mouvements) {
                    val mouvObj = JSONObject()
                    mouvObj.put("titre", mouv.titre)
                    mouvObj.put("montant", mouv.montant.toString())
                    mouvObj.put("categorie", mouv.categorie)
                    mouvObj.put("date", mouv.date.toString())
                    mouvObj.put("type", mouv.type.name)
                    mouvementsArray.put(mouvObj)
                }
                jsonObject.put("mouvements", mouvementsArray)

                jsonArray.put(jsonObject)
            }
            val file = File(context.filesDir, fileName)
            file.writeText(jsonArray.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}