package me.kmmiller.theduckypodcast.models

import com.google.firebase.firestore.DocumentSnapshot
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import me.kmmiller.theduckypodcast.models.interfaces.RModel
import me.kmmiller.theduckypodcast.utils.getStringArrayList
import me.kmmiller.theduckypodcast.utils.nonNullLong
import me.kmmiller.theduckypodcast.utils.nonNullString

@RealmClass
open class SeriesModel: RealmObject(), RModel {
    @Required
    @PrimaryKey
    var id: String = ""

    var title = ""
    var description = ""
    var expandedDescription = ""
    var podcastLink = ""
    var researchLinks = RealmList<String>()
    var season = 0L

    override fun toRealmModel(document: DocumentSnapshot) {
        id = document.id
        title = document["title"].nonNullString()
        description = document["description"].nonNullString()
        expandedDescription = document["expandedDescription"].nonNullString()
        podcastLink = document["podcastLink"].nonNullString()
        researchLinks.addAll(document.getStringArrayList("researchLinks"))
        season = document["season"].nonNullLong()
    }

    override fun fromRealmModel(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["title"] = title
        map["description"] = description
        map["expandedDescription"] = expandedDescription
        map["podcastLink"] = podcastLink
        map["researchLinks"] = researchLinks
        map["season"] = season
        return map
    }
}