package com.example.testapp

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.testapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // получает телефонную книгу из c++ части и сохраняет отдельно, чтобы хранить информацию на протяжении всей жизни приложения
    private val mainViewModel: MainViewModel by viewModels { MainViewModelFactory(getPhonebook()) }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textViewTopName.text = mainViewModel.phonebook[0].name
        binding.textViewTopNumber.text = mainViewModel.phonebook[0].number

        // запоминает id последнего добавленного элемента чтобы прикреплять к нему последующий
        var previousNameViewId = binding.textViewTopName.id
        var previousNumberViewId = binding.textViewTopNumber.id

        // высчитывает внутренние границы элементов в dp
        val padding = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            5f,
            resources.displayMetrics
        ).toInt()

        for (i in 1 until mainViewModel.phonebook.size) {

            previousNameViewId = addTextView(
                true,
                mainViewModel.phonebook[i].name,
                previousNameViewId,
                padding
            )

            previousNumberViewId = addTextView(
                false,
                mainViewModel.phonebook[i].number,
                previousNumberViewId,
                padding
            )
        }
    }

    private fun addTextView(is_name: Boolean, text: String, previousId: Int, padding: Int): Int {

        val tv = TextView(this).apply {
            id = View.generateViewId()
            this.text = text
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setPadding(padding)
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val layout = if (is_name) binding.root else binding.constraintLayoutNumber
        layout.addView(tv)

        val cs = ConstraintSet()
        cs.clone(layout)
        cs.connect(
            tv.id,
            if (is_name) ConstraintSet.START else ConstraintSet.END,
            layout.id,
            if (is_name) ConstraintSet.START else ConstraintSet.END
        )
        cs.connect(
            tv.id,
            if (is_name) ConstraintSet.END else ConstraintSet.START,
            binding.guidelineMainCenter.id,
            ConstraintSet.START
        )
        cs.connect(tv.id, ConstraintSet.TOP, previousId, ConstraintSet.BOTTOM)
        cs.applyTo(layout)

        return tv.id
    }

    fun goToInfo(view: View) {
        startActivity(Intent(this, InfoActivity::class.java))
    }

    private external fun getPhonebook(): Array<PhonebookEntry>

    companion object {
        init {
            System.loadLibrary("testapp")
        }
    }
}

class MainViewModel(val phonebook: Array<PhonebookEntry>) : ViewModel()

class MainViewModelFactory(private val phonebook: Array<PhonebookEntry>) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Array<PhonebookEntry>::class.java).newInstance(phonebook)
    }
}

class PhonebookEntry(val name: String, val number: String)