package app.time_to.timeto.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.time_to.timeto.toColor
import timeto.shared.TextFeatures

@Composable
fun TextFeaturesTriggersView(
    triggers: List<TextFeatures.Trigger>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {

    if (triggers.isEmpty())
        return

    val itemHeight = 26.dp
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        itemsIndexed(
            triggers,
            key = { _, checklist -> checklist.id }
        ) { _, trigger ->
            val isLast = triggers.last() == trigger
            Row(
                modifier = Modifier
                    .padding(end = if (isLast) 0.dp else 8.dp)
                    .height(itemHeight)
                    .clip(MySquircleShape(len = 50f))
                    .background(trigger.color.toColor())
                    .clickable {
                        trigger.performUI()
                    }
                    .padding(start = 8.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    trigger.title,
                    modifier = Modifier
                        .offset(y = (-0.8).dp),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400,
                    color = c.white
                )
            }
        }
    }
}
