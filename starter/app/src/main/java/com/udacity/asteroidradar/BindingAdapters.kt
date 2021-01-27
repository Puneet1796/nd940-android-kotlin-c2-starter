package com.udacity.asteroidradar

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.udacity.asteroidradar.api.Asteroid
import com.udacity.asteroidradar.main.AsteroidAdapter
import timber.log.Timber

@BindingAdapter("listOfAsteroids")
fun bindRecyclerView(recyclerView: RecyclerView, listOfAsteroids: List<Asteroid>?) {
    val adapter = recyclerView.adapter as AsteroidAdapter
    listOfAsteroids?.let {
        adapter.submitList(it)
    }
}

@BindingAdapter("imageUrl")
fun bindImageView(imageView: ImageView, url: String?) {
    Timber.i("Image URL: $url")
    Glide.with(imageView.context)
        .load(url)
        .placeholder(R.drawable.loading_animation)
        .error(R.drawable.ic_broken_image)
        .into(imageView)
}

@BindingAdapter("contentDescription")
fun bind(imageView: ImageView, isHazardous: Boolean) {
    val res = imageView.context.resources
    if (isHazardous) {
        imageView.contentDescription = res.getString(
            R.string.asteroid_status_image_description,
            res.getString(R.string.potentially_hazardous)
        )
    } else {
        imageView.contentDescription = res.getString(
            R.string.asteroid_status_image_description,
            res.getString(R.string.not_potentially_hazardous)
        )
    }
}

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    val res = imageView.context.resources
    if (isHazardous) {
        imageView.contentDescription =
            res.getString(
                R.string.asteroid_status_description,
                res.getString(R.string.potentially_hazardous)
            )
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        imageView.contentDescription =
            res.getString(
                R.string.asteroid_status_description,
                res.getString(R.string.not_potentially_hazardous)
            )
        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}
