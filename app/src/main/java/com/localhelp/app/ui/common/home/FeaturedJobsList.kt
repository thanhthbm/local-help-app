package com.localhelp.app.ui.common.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.localhelp.app.R

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import coil.compose.AsyncImage
import com.localhelp.app.model.response.JobResponse
import java.text.DecimalFormat

@Composable
fun FeaturedJobsList(
    featuredJobs: List<JobResponse>,
    onJobClick: (JobResponse) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(featuredJobs) { job ->
            FeaturedJobCard(job = job, onClick = { onJobClick(job) })
        }
    }
}

@Composable
fun FeaturedJobCard(
    job: JobResponse,
    onClick: () -> Unit
){
    val currencyFormat = DecimalFormat("#,###đ")

    Card(
        modifier = Modifier
            .size(width = 280.dp, height = 180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(){
            val imageUrl = job.images?.firstOrNull() ?: R.drawable.welcome_image
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.welcome_image),
                placeholder = painterResource(R.drawable.welcome_image)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))))
                    .padding(12.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = job.title ?: "Không có tiêu đề",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = currencyFormat.format(job.price ?: 0.0),
                        color = Color(0xFFED7D68),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = job.categoryName ?: "",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
