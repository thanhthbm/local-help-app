package com.localhelp.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.localhelp.app.data.local.LocalUser
import com.localhelp.app.ui.common.home.CategorySection
import com.localhelp.app.ui.common.home.FeaturedJobsList
import com.localhelp.app.ui.common.home.HomeHeader
import com.localhelp.app.ui.common.home.RecentJobCard
import com.localhelp.app.ui.common.home.SearchBar
import com.localhelp.app.ui.common.home.SectionHeader

@Composable
fun HomeScreen() {
    val user = LocalUser.current

    var scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFDFD))
            .verticalScroll(scrollState)
    ) {
        HomeHeader(user?.fullName ?: "Name", "Hà Nội")

        SearchBar(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        CategorySection()

        SectionHeader(
            title = "Nổi bật quanh bạn",
            onSeeMore = {/* TODO*/}
        )

        FeaturedJobsList()

        SectionHeader(
            title = "Việc mới nhất",
            onSeeMore = {/* TODO */}
        )

        repeat(5){
            RecentJobCard()
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}