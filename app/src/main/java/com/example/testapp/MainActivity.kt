package com.example.testapp

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

        val phonebook = getPhonebook()

        binding.textViewTopName.text = phonebook[0].name
        binding.textViewTopNumber.text = phonebook[0].number
        var previousNameViewId = binding.textViewTopName.id
        var previousNumberViewId = binding.textViewTopNumber.id
        val padding =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics)
                .toInt()

        for (i in 1 until phonebook.size) {
            previousNameViewId = addTextView(
                phonebook[i].name, padding,
                previousNameViewId, true
            )
            previousNumberViewId = addTextView(
                phonebook[i].number, padding,
                previousNumberViewId, false
            )
        }
    }

    private fun addTextView(text: String, padding: Int, previousId: Int, is_name: Boolean): Int {

        val tv = TextView(this)
        tv.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        if (is_name) binding.root.addView(tv) else binding.constraintLayoutNumber.addView(tv)

        tv.id = View.generateViewId()
        tv.text = text
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        tv.setPadding(padding)

        if (is_name) connectNameView(tv, previousId) else connectNumberView(tv, previousId)
        return tv.id
    }

    private fun connectNameView(tv: TextView, previousId: Int) {
        val cs = ConstraintSet()
        cs.clone(binding.root)
        cs.connect(
            tv.id,
            ConstraintSet.START,
            binding.root.id,
            ConstraintSet.START
        )
        cs.connect(
            tv.id,
            ConstraintSet.END,
            binding.guidelineCenter.id,
            ConstraintSet.START
        )
        cs.connect(
            tv.id,
            ConstraintSet.TOP,
            previousId,
            ConstraintSet.BOTTOM
        )
        cs.applyTo(binding.root)
    }

    private fun connectNumberView(tv: TextView, previousId: Int) {
        val cs = ConstraintSet()
        cs.clone(binding.constraintLayoutNumber)
        cs.connect(
            tv.id,
            ConstraintSet.END,
            binding.constraintLayoutNumber.id,
            ConstraintSet.END
        )
        cs.connect(
            tv.id,
            ConstraintSet.START,
            binding.constraintLayoutNumber.id,
            ConstraintSet.START
        )
        cs.connect(
            tv.id,
            ConstraintSet.TOP,
            previousId,
            ConstraintSet.BOTTOM
        )
        cs.applyTo(binding.constraintLayoutNumber)
    }

    private external fun getPhonebook(): Array<PhonebookEntry>

    companion object {
        // Used to load the 'testapp' library on application startup.
        init {
            System.loadLibrary("testapp")
        }
    }
}

class PhonebookEntry(val name: String, val number: String)