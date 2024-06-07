package com.example.laughlines.utils.extensions

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("bindData:url")
fun setImage(imageView: ImageView, link: String?) {
    link?.let { Glide.with(imageView.context).load(link).into(imageView) }
}

@BindingAdapter("bindData:src")
fun setImageViewResource(imageView: ImageView, resource: Int) {
    imageView.setImageResource(resource)
}

@BindingAdapter("bindData:bitmap")
fun setImageViewBitmap(imageView: ImageView, bitmap: Bitmap?) {
    bitmap?.let { Glide.with(imageView.context).asBitmap().load(bitmap).into(imageView) }
}