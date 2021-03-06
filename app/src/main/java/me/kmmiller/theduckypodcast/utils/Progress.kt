package me.kmmiller.theduckypodcast.utils

import me.kmmiller.theduckypodcast.base.BaseActivity
import me.kmmiller.theduckypodcast.base.ui.BaseProgress
import me.kmmiller.theduckypodcast.main.interfaces.ICancel

class Progress(private val activity: BaseActivity) {
    private var progressBar: BaseProgress? = null

    fun progress(message: String) {
        progress(message, null)
    }

    fun progress(message: String, canceler: ICancel?) {
        if(progressBar == null) progressBar = BaseProgress(activity)

        progressBar?.setCanceledOnTouchOutside(canceler != null)
        progressBar?.setCancelable(canceler != null)

        progressBar?.setOnCancelListener {
            progressBar = null
            canceler?.onCancel()
        }

        progressBar?.setMessage(message)
        progressBar?.show()
    }

    fun dismiss() {
        progressBar?.dismiss()
        progressBar = null
    }
}