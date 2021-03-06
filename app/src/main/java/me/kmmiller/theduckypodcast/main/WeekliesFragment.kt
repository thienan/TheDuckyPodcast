package me.kmmiller.theduckypodcast.main

import android.os.Bundle
import android.view.*
import com.google.firebase.firestore.FirebaseFirestore
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseFragment
import me.kmmiller.theduckypodcast.databinding.WeekliesFragmentBinding
import me.kmmiller.theduckypodcast.main.interfaces.NavItem
import me.kmmiller.theduckypodcast.main.interfaces.SavableFragment

class WeekliesFragment: BaseFragment(), NavItem, SavableFragment {
    private lateinit var binding: WeekliesFragmentBinding

    var menu: Menu? = null

    override fun getTitle(): String = getString(R.string.weeklies)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = WeekliesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onSave() {
        // TODO
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

    override fun getNavId(): Int = R.id.nav_weeklies

    companion object {
        const val TAG = "weeklies_fragment"
    }
}