package com.bgt.amoebadotsindicator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.TextView

class DemoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val textView = TextView(requireContext())
        textView.textSize = 40f
        textView.text = "Page ${arguments?.getInt(ARG_POSITION)}"
        textView.gravity = android.view.Gravity.CENTER
        return textView
    }

    companion object {
        private const val ARG_POSITION = "position"

        fun newInstance(position: Int) = DemoFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_POSITION, position)
            }
        }
    }
}
