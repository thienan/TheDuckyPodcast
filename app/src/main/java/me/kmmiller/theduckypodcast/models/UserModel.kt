package me.kmmiller.theduckypodcast.models

import com.google.firebase.firestore.DocumentSnapshot
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import me.kmmiller.theduckypodcast.models.interfaces.RModel
import me.kmmiller.theduckypodcast.utils.nonNullLong
import me.kmmiller.theduckypodcast.utils.nonNullString

@RealmClass
open class UserModel : RealmObject(), RModel {
    @Required
    @PrimaryKey
    var id: String = ""

    var email = ""
    var age = 0L
    var gender = ""
    var state = ""

    override fun toRealmModel(document: DocumentSnapshot) {
        id = document.id
        email = document["email"].nonNullString()
        age = document["age"].nonNullLong()
        gender = document["gender"].nonNullString()
        state = document["state"].nonNullString()
    }

    override fun fromRealmModel(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["email"] = email
        map["age"] = age
        map["gender"] = gender
        map["state"] = state
        return map
    }

    companion object {
        @JvmField
        val stateAbbreviationsList = arrayOf("AL","AK", "AZ", "AR", "CA",
            "CO", "CT", "DE", "FL","GA", "HI", "ID", "IL", "IN", "IA", "KS","KY",
            "LA", "MA", "ME", "MD", "MI", "MN", "MS", "MO", "MT", "NE", "NV",
            "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI",
            "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY")

        @JvmStatic
        fun getPositionOfGender(key: String): Int {
            return when(key.toLowerCase()) {
                "male" -> 1
                "female" -> 2
                "non-binary" -> 3
                else -> 0
            }
        }
    }
}