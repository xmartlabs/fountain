package com.xmartlabs.sample.ui.selectmode

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xmartlabs.sample.R
import com.xmartlabs.sample.ui.common.FountainAdapterType
import com.xmartlabs.sample.ui.searchusers.ListGithubUsersActivity
import kotlinx.android.synthetic.main.activity_select_library_adapter.*

class SelectFountainModeActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_select_library_adapter)

    coroutineAdapterButton.setOnClickListener { goToListGithubUsersActivity(FountainAdapterType.COROUTINE) }
    retrofitButton.setOnClickListener { goToListGithubUsersActivity(FountainAdapterType.RETROFIT) }
    rxjavaButton.setOnClickListener { goToListGithubUsersActivity(FountainAdapterType.RX) }
  }

  private fun goToListGithubUsersActivity(type: FountainAdapterType) {
    val intent = Intent(this, ListGithubUsersActivity::class.java)
        .putExtra(ListGithubUsersActivity.KEY_ADAPTER_TYPE_NAME, type)
    startActivity(intent)
  }
}
