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

        // задает кнопке запуск окна с инфой о системе
        binding.buttonToInfo.setOnClickListener {
            startActivity(Intent(this, InfoActivity::class.java))
        }

        // высчитывает внутренние границы элементов в dp
        val padding = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            5f,
            resources.displayMetrics
        ).toInt()

        // запоминает id последнего добавленного элемента чтобы прикреплять к нему последующий
        var previousNameViewId = addFirstTextView(binding.constraintLayoutNames, mainViewModel.phonebook[0].name, padding)
        var previousNumberViewId = addFirstTextView(binding.constraintLayoutNumbers, mainViewModel.phonebook[0].number, padding)

        for (i in 1 until mainViewModel.phonebook.size) {

            previousNameViewId = addTextView(
                binding.constraintLayoutNames,
                mainViewModel.phonebook[i].name,
                previousNameViewId,
                padding
            )

            previousNumberViewId = addTextView(
                binding.constraintLayoutNumbers,
                mainViewModel.phonebook[i].number,
                previousNumberViewId,
                padding
            )
        }
    }

    private fun createTextView(text: String, padding: Int): TextView {
        return TextView(this).apply {
            id = View.generateViewId()
            this.text = text
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setPadding(padding)
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun addFirstTextView(layout: ConstraintLayout, text: String, padding: Int): Int {

        val tv = createTextView(text, padding)
        layout.addView(tv)

        val cs = ConstraintSet()
        cs.clone(layout)
        cs.connect(tv.id, ConstraintSet.START, layout.id, ConstraintSet.START)
        cs.connect(tv.id, ConstraintSet.END, layout.id, ConstraintSet.END)
        cs.connect(tv.id, ConstraintSet.TOP, layout.id, ConstraintSet.TOP)
        cs.applyTo(layout)

        return tv.id
    }

    private fun addTextView(layout: ConstraintLayout, text: String, previousId: Int, padding: Int): Int {

        val tv = createTextView(text, padding)
        layout.addView(tv)

        val cs = ConstraintSet()
        cs.clone(layout)
        cs.connect(tv.id, ConstraintSet.START, layout.id, ConstraintSet.START)
        cs.connect(tv.id, ConstraintSet.END, layout.id, ConstraintSet.END)
        cs.connect(tv.id, ConstraintSet.TOP, previousId, ConstraintSet.BOTTOM)
        cs.applyTo(layout)

        return tv.id
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