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

package com.raywenderlich.android.authorizeme.signup

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raywenderlich.android.authorizeme.R
import com.raywenderlich.android.authorizeme.common.Result
import com.raywenderlich.android.authorizeme.common.data.AuthRepository
import com.raywenderlich.android.authorizeme.common.ui.model.AuthResult
import com.raywenderlich.android.authorizeme.common.ui.model.AuthUserView

class SignUpViewModel(private val authRepository: AuthRepository) : ViewModel() {

  private val _signupForm = MutableLiveData<SignUpFormState>()
  val signupFormState: LiveData<SignUpFormState> = _signupForm

  private val _signupResult = MutableLiveData<AuthResult>()
  val signupResult: LiveData<AuthResult> = _signupResult

  fun signup(fullname: String, username: String, password: String) {
    // can be launched in a separate asynchronous job
    val result = authRepository.signup(fullname, username, password)

    if (result is Result.Success) {
      _signupResult.value = AuthResult(
          success = AuthUserView(displayName = result.data.displayName))
    } else {
      _signupResult.value = AuthResult(error = R.string.login_failed)
    }
  }

  fun signupDataChanged(fullname: String, username: String, password: String) {
    if (!isFullNameValid(fullname)) {
      _signupForm.value = SignUpFormState(fullnameError = R.string.invalid_full_name)
    } else if (!isUserNameValid(username)) {
      _signupForm.value = SignUpFormState(usernameError = R.string.invalid_username)
    } else if (!isPasswordValid(password)) {
      _signupForm.value = SignUpFormState(passwordError = R.string.invalid_password)
    } else {
      _signupForm.value = SignUpFormState(isDataValid = true)
    }
  }

  // A placeholder fullname validation check
  private fun isFullNameValid(fullname: String): Boolean {
    return fullname.isNotBlank()
  }

  // A placeholder username validation check
  private fun isUserNameValid(username: String): Boolean {
    return if (username.contains("@")) {
      Patterns.EMAIL_ADDRESS.matcher(username).matches()
    } else {
      username.isNotBlank()
    }
  }

  // A placeholder password validation check
  private fun isPasswordValid(password: String): Boolean {
    return password.length > 5
  }
}