package me.kmmiller.theduckypodcast.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.google.firebase.firestore.DocumentSnapshot
import java.lang.Exception

fun DocumentSnapshot?.getStringArrayList(element: String) : ArrayList<String> {
    val ary = ArrayList<String>()

    this?.apply {
        this[element]?.let {
            return try {
                ary.addAll(it as ArrayList<String>)
                ary
            } catch(e: Exception) {
                ary
            }
        }
    }

    return ary
}

fun DocumentSnapshot?.getHashMap(element: String) : HashMap<String, ArrayList<String>> {
    var map = HashMap<String, ArrayList<String>>()

    this?.apply {
        this[element]?.let {
            return try {
                map = it as HashMap<String, ArrayList<String>>
                map
            } catch(e: Exception) {
                map
            }
        }
    }

    return map
}

fun AppCompatEditText.onTextChangedListener(onTextChanged: (e: Editable?) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(e: Editable?) {
            onTextChanged.invoke(e)
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
    })
}

fun Any?.nonNullString(): String {
    if(this == null) return ""

    return try {
        (this as? String) ?: ""
    } catch (e: Exception) {
        ""
    }
}

fun Any?.nonNullLong(): Long {
    if(this == null) return 0L

    return try {
        (this as? Long) ?: 0L
    } catch (e: Exception) {
        0L
    }
}