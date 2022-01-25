package com.example.testapp

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.setPadding
import com.example.testapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // получает телефонную книгу из c++ части
        val phonebook = getPhonebook()

        binding.textViewTopName.text = phonebook[0].name
        binding.textViewTopNumber.text = phonebook[0].number

        // запоминает id последнего добавленного элемента чтобы прикреплять к нему последующий
        var previousNameViewId = binding.textViewTopName.id
        var previousNumberViewId = binding.textViewTopNumber.id

        // высчитывает внутренние границы элементов в dp
        val padding = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            5f,
            resources.displayMetrics
        ).toInt()

        for (i in 1 until phonebook.size) {

            previousNameViewId = addTextView(
                phonebook[i].name,
                padding,
                previousNameViewId,
                true
            )

            previousNumberViewId = addTextView(
                phonebook[i].number,
                padding,
                previousNumberViewId,
                false
            )
        }
    }

    private fun addTextView(text: String, padding: Int, previousId: Int, is_name: Boolean): Int {

        val tv = TextView(this)

        tv.id = View.generateViewId()
        tv.text = text
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        tv.setPadding(padding)
        tv.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        connectTextView(
            tv,
            if (is_name) binding.root else binding.constraintLayoutNumber,
            previousId,
            is_name
        )

        return tv.id
    }

    private fun connectTextView(tv: TextView, layout: ConstraintLayout, previousId: Int, is_name: Boolean) {

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
            binding.guidelineCenter.id,
            ConstraintSet.START
        )

        cs.connect(
            tv.id,
            ConstraintSet.TOP,
            previousId,
            ConstraintSet.BOTTOM
        )

        cs.applyTo(layout)
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

class PhonebookEntry(val name: String, val number: String)