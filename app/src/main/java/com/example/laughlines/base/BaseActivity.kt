package com.example.laughlines.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.laughlines.utils.Constant

abstract class BaseActivity<VB : ViewDataBinding> : AppCompatActivity() {
    protected lateinit var binding: VB
    abstract val layoutID: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutID)
        setContentView(binding.root)
        initView()
        initAction()
    }

    open fun initView() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    open fun initAction() {}

    fun goTo(context: Context, java: Class<*>) {
        startActivity(Intent(context, java))
    }

    fun goTo(context: Context, java: Class<*>, value: Bundle) {
        val intent = Intent(context, java)
        intent.putExtra(Constant.INTENT_KEY, value)
        startActivity(intent)
    }

    fun goTo(context: Context, java: Class<*>, value: String) {
        val intent = Intent(context, java)
        intent.putExtra(Constant.INTENT_KEY, value)
        startActivity(intent)
    }

    fun notify(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun setNavigationBarColor(color:Int){
        val decorView = window.decorView
        decorView.systemUiVisibility = decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        window.navigationBarColor = ContextCompat.getColor(this, color)
    }

    fun setLayoutNoLimit(){
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

}