package me.kmmiller.theduckypodcast.main

import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseFragment
import me.kmmiller.theduckypodcast.core.findDailiesModel
import me.kmmiller.theduckypodcast.databinding.DailiesFragmentBinding
import me.kmmiller.theduckypodcast.models.DailiesModel
import me.kmmiller.theduckypodcast.utils.nonNullString

class DailiesFragment : BaseFragment(), NavItem, SavableFragment {
    private lateinit var binding: DailiesFragmentBinding
    private lateinit var fb: FirebaseFirestore

    var menu: Menu? = null

    private var adapter: QuestionAnswerAdapter? = null

    override fun getTitle(): String = getString(R.string.dailies)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DailiesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        viewModel?.let {
            if(it.dailyId.isNotEmpty()) {
                load(it.dailyId)
            }
        }

        if(viewModel?.dailyId.nonNullString().isEmpty()) {
            showCancelableProgress(getString(R.string.loading))
        }

        fb = FirebaseFirestore.getInstance()
        getDailyId(fb) {
            if(context != null) {
                if (it != viewModel?.dailyId.nonNullString()) {
                    viewModel?.dailyId = it

                    showCancelableProgress(getString(R.string.loading))
                    fb.collection("dailies")
                        .document(it)
                        .get()
                        .addOnSuccessListener { doc ->
                            val model = DailiesModel()
                            model.toRealmModel(doc)
                            realm?.executeTransaction { rm ->
                                rm.copyToRealmOrUpdate(model)
                            }
                            load(it)
                        }
                        .addOnFailureListener { e ->
                            handleError(e)
                            dismissProgress()
                        }
                } else {
                    dismissProgress()
                }
            }
        }
    }

    private fun load(id: String) {
        try {
            realm?.findDailiesModel(id)?.let {
                binding.title.text = it.title

                adapter = QuestionAnswerAdapter(ArrayList(it.items))
                binding.questionAnswerList.adapter = adapter
                binding.questionAnswerList.layoutManager = LinearLayoutManager(requireContext())
                binding.questionAnswerList.isNestedScrollingEnabled = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Context probably null")
        }
        dismissProgress()
    }

    private fun getDailyId( fb: FirebaseFirestore, onComplete: (String) -> Unit) {
        fb.collection("dailies")
            .document("get-daily-id")
            .get()
            .addOnSuccessListener {
                val id = it.get("id").nonNullString()
                onComplete.invoke(id)
            }
            .addOnFailureListener {
                dismissProgress()
                handleError(it)
            }
    }

    private fun validateAnswers(answers: SparseArray<Pair<Int, String?>>): Boolean {
        for(i in 0 until answers.size()) {
            val answer = answers.get(i)

            val answerPosition = answer.first
            val otherInput = answer.second

            if(answerPosition == -1) {
                // -1 indicates that no answer was given for that question
                binding.scrollView.scrollTo(0, 0) // Scroll to top so error is visible
                binding.answerError.visibility = View.VISIBLE
                return false
            } else if(otherInput != null && otherInput.isEmpty()) {
                // Empty answer for "other" selection
                return false
            }
        }
        return true
    }

    override fun onSave() {
        val answers = adapter?.getAnswers() ?: return
        val additionalComments = binding.additionalComments.text?.toString().nonNullString()

        fun save(dailyId: String) {
            showProgress(getString(R.string.saving))
            if(validateAnswers(answers)) {
                auth?.uid?.let { userId ->
                    val submittableModel = DailiesModel.createSubmittableModel(dailyId, answers, additionalComments)
                    // Send the data to server after it has been validated, disable save and show results
                    val fb = FirebaseFirestore.getInstance()
                    fb.collection("dailies-responses")
                        .document(userId)
                        .set(submittableModel)
                        .addOnSuccessListener {
                            val dailiesModel = realm?.findDailiesModel(viewModel?.dailyId)
                            if (dailiesModel != null) {
                                realm?.executeTransaction {
                                    dailiesModel.isSubmitted = true
                                }
                            }
                            dismissProgress()
                        }
                        .addOnFailureListener { e ->
                            handleError(e)
                            dismissProgress()
                        }
                }
            } else {
                dismissProgress()
            }
        }

        val dailyId = viewModel?.dailyId
        if(dailyId == null) {
            getDailyId(fb) {
                save(it)
            }
        } else {
            save(dailyId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.savable_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.save).title = getString(R.string.submit).toUpperCase()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item != null && item.itemId == R.id.save) {
            onSave()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getNavId(): Int = R.id.nav_home

    companion object {
        const val TAG = "dailies_fragment"
    }
}