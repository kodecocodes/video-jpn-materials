/*
 * Copyright (c) 2021 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.authorizeme.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.raywenderlich.android.authorizeme.R
import com.raywenderlich.android.authorizeme.common.ui.model.AuthUserView
import com.raywenderlich.android.authorizeme.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

  private lateinit var loginViewModel: LoginViewModel
  private var _binding: FragmentLoginBinding? = null
  private val binding get() = _binding!!

  private var user = ""

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    _binding = FragmentLoginBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
        .get(LoginViewModel::class.java)

    binding.welcome.isEnabled = false

    loginViewModel.loginFormState.observe(viewLifecycleOwner,
        Observer { loginFormState ->
          if (loginFormState == null) {
            return@Observer
          }
          binding.login.isEnabled = loginFormState.isDataValid
          loginFormState.usernameError?.let {
            binding.username.error = getString(it)
          }
          loginFormState.passwordError?.let {
            binding.password.error = getString(it)
          }
        })

    loginViewModel.authResult.observe(viewLifecycleOwner,
        Observer { loginResult ->
          loginResult ?: return@Observer
          binding.loading.visibility = View.GONE
          loginResult.error?.let {
            showLoginFailed(it)
          }
          loginResult.success?.let {
            loginSuccessAction(it)
          }
        })

    val afterTextChangedListener = object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        // ignore
      }

      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        // ignore
      }

      override fun afterTextChanged(s: Editable) {
        loginViewModel.loginDataChanged(
            binding.username.text.toString(),
            binding.password.text.toString()
        )
      }
    }
    binding.username.addTextChangedListener(afterTextChangedListener)
    binding.password.addTextChangedListener(afterTextChangedListener)

    binding.login.setOnClickListener {
      binding.loading.visibility = View.VISIBLE
      initLogin()
    }

    binding.welcome.setOnClickListener {
    }
  }

  private fun initLogin() {
    loginViewModel.login(
        binding.username.text.toString(),
        binding.password.text.toString()
    )
  }

  private fun loginSuccessAction(model: AuthUserView) {
    binding.login.isEnabled = false
    binding.welcome.isEnabled = true
    user = model.displayName
    val welcome = getString(R.string.welcome) + " " + model.displayName
    Toast.makeText(activity, welcome, Toast.LENGTH_LONG).show()
  }

  private fun showLoginFailed(@StringRes errorString: Int) {
    val appContext = context?.applicationContext ?: return
    Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}