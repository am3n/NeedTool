package ir.am3n.needtool

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope

abstract class BackPressHandler : Fragment() {
    var created: Long = System.currentTimeMillis()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        created = System.currentTimeMillis()
        return null
    }
    abstract fun onBackPressed(): Boolean
    val fm: FragmentManager? get() = activity?.supportFragmentManager
}

fun FragmentActivity.consumeBackPress(): Boolean {
    var consumed = false
    supportFragmentManager.fragments
        .filterIsInstance<BackPressHandler>()
        .sortedByDescending { it.created }
        .forEach {
            if (!consumed) {
                it.onBackPressed().let { flag -> consumed = flag }
            }
        }
    return consumed
}



fun View.onBackPressed(backPressHandler: BackPressHandler?) {
    this.isFocusableInTouchMode = true
    this.requestFocus()
    this.setOnKeyListener { _, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            backPressHandler?.onBackPressed()
            return@setOnKeyListener true
        }
        return@setOnKeyListener false
    }
}

fun View.onBackPressed(callback: () -> Unit) {
    this.isFocusableInTouchMode = true
    this.requestFocus()
    this.setOnKeyListener { _, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            callback()
            return@setOnKeyListener true
        }
        return@setOnKeyListener false
    }
}


val Fragment.vlo: LifecycleOwner get() = viewLifecycleOwner
val Fragment.vloScope: LifecycleCoroutineScope get() = vlo.lifecycleScope


val Fragment.sfm: FragmentManager get() = requireActivity().sfm
val FragmentActivity.sfm: FragmentManager get() = supportFragmentManager
val Fragment.cfm: FragmentManager get() = childFragmentManager


val Fragment.TAG: String get() = javaClass.name


fun FragmentTransaction.replaceAndStack(@IdRes containerViewId: Int, @NonNull fragment: Fragment): FragmentTransaction {
    replace(containerViewId, fragment, fragment.TAG)
    addToBackStack(fragment.TAG)
    return this
}

fun FragmentTransaction.addAndStack(@IdRes containerViewId: Int, @NonNull fragment: Fragment): FragmentTransaction {
    add(containerViewId, fragment, fragment.TAG)
    addToBackStack(fragment.TAG)
    return this
}

fun FragmentManager.popBackStackInclusive(tag: String) {
    popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
}

fun FragmentManager.popBackStackInclusive(clazz: Class<out Fragment>) {
    popBackStackInclusive(clazz.newInstance().TAG)
}

fun AppCompatActivity.addAndHideFragment(containerId: Int, fragment: Fragment, addToBackStack: Boolean) {
    val ft = supportFragmentManager.beginTransaction()
    ft.add(containerId, fragment, fragment.TAG)
    ft.hide(fragment)
    if (addToBackStack) {
        ft.addToBackStack(fragment.TAG)
    }
    ft.commit()
}

fun AppCompatActivity.showOrAddFragment(containerId: Int, fragment: Fragment, addToBackStack: Boolean) {
    if (supportFragmentManager.findFragmentByTag(fragment.TAG) != null) {
        showFragment(fragment)
    } else {
        addFragment(containerId, fragment, addToBackStack)
    }
}

fun AppCompatActivity.showFragment(fragment: Fragment) {
    val ft = supportFragmentManager.beginTransaction()
    ft.show(fragment)
    ft.commit()
}

fun AppCompatActivity.hideFragment(fragment: Fragment) {
    val ft = supportFragmentManager.beginTransaction()
    ft.hide(fragment)
    ft.commit()
}

fun AppCompatActivity.addFragment(containerId: Int, fragment: Fragment, addToBackStack: Boolean) {
    val fragmentTransaction = supportFragmentManager.beginTransaction()
    performAddFragment(containerId, fragmentTransaction, fragment, addToBackStack)
}

private fun performAddFragment(containerId: Int, ft: FragmentTransaction, fragment: Fragment, addToBackStack: Boolean) {
    ft.add(containerId, fragment, fragment.TAG)
    if (addToBackStack) {
        ft.addToBackStack(fragment.TAG)
    }
    ft.commit()
}

fun AppCompatActivity.replaceFragment(containerId: Int, fragment: Fragment, addToBackStack: Boolean) {
    val fragmentTransaction = supportFragmentManager.beginTransaction()
    performReplaceFragment(containerId, fragmentTransaction, fragment, addToBackStack)
}

private fun performReplaceFragment(containerId: Int, ft: FragmentTransaction, fragment: Fragment, addToBackStack: Boolean) {
    ft.replace(containerId, fragment, fragment.TAG)
    if (addToBackStack) {
        ft.addToBackStack(fragment.TAG)
    }
    ft.commit()
}