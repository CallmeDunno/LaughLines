package com.example.laughlines.view.change_pass_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.laughlines.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password_detail, container, false)
    }

}