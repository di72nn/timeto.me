package me.timeto.app.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.timeto.app.*
import me.timeto.app.R
import me.timeto.shared.ColorRgba
import me.timeto.shared.vm.ReadmeSheetVM

private val imagesHBetween = 4.dp
private val imagesHBlock = 14.dp
private val imagesShape = SquircleShape(len = 50f)

private val pTextLineHeight = 22.sp // 1.38 ratio

@Composable
fun ReadmeSheet(
    layer: WrapperView.Layer,
) {

    val (_, state) = rememberVM { ReadmeSheetVM() }

    VStack(
        modifier = Modifier
            .background(c.bg)
    ) {

        val scrollState = rememberScrollState()

        Fs__HeaderTitle(
            title = state.title,
            scrollState = scrollState,
            onClose = {
                layer.close()
            },
        )

        VStack(
            modifier = Modifier
                .verticalScroll(state = scrollState)
                .padding(bottom = 20.dp)
                .weight(1f),
        ) {

            state.paragraphs.forEachIndexed { idx, paragraph ->

                val prevP: ReadmeSheetVM.Paragraph? =
                    if (idx == 0) null else state.paragraphs[idx - 1]

                when (paragraph) {

                    is ReadmeSheetVM.Paragraph.Title -> PTitleView(paragraph.text, prevP)

                    is ReadmeSheetVM.Paragraph.Text -> PTextView(paragraph.text, prevP)

                    is ReadmeSheetVM.Paragraph.TextHighlight -> PTextHighlightView(paragraph.text, prevP)

                    is ReadmeSheetVM.Paragraph.ListDash -> PListDashedView(paragraph.items)

                    is ReadmeSheetVM.Paragraph.TimerTypical -> {
                        ImagePreviewsView(
                            R.drawable.readme_timer_1,
                        )
                    }

                    is ReadmeSheetVM.Paragraph.TimerMyActivities -> {
                        ImagePreviewsView(
                            R.drawable.readme_activities_1,
                        )
                    }

                    is ReadmeSheetVM.Paragraph.TimerCharts -> {
                        ImagePreviewsView(
                            R.drawable.readme_chart_1,
                            R.drawable.readme_chart_2,
                            R.drawable.readme_chart_3,
                        )
                    }

                    is ReadmeSheetVM.Paragraph.TimerPractice1 -> {
                        ImagePreviewsView(
                            R.drawable.readme_timer_practice_1,
                            R.drawable.readme_timer_practice_2,
                            R.drawable.readme_timer_practice_3,
                            R.drawable.readme_timer_practice_4,
                        )
                    }

                    is ReadmeSheetVM.Paragraph.TimerPractice2 -> {
                        ImagePreviewsView(
                            R.drawable.readme_timer_practice_5,
                            R.drawable.readme_chart_2,
                            R.drawable.readme_chart_3,
                        )
                    }

                    is ReadmeSheetVM.Paragraph.RepeatingsMy -> {
                        ImagePreviewsView(
                            R.drawable.readme_repeatings_1,
                        )
                    }


                    is ReadmeSheetVM.Paragraph.RepeatingsToday -> {
                        ImagePreviewsView(
                            R.drawable.readme_repeatings_2,
                        )
                    }


                    is ReadmeSheetVM.Paragraph.RepeatingsPractice1 -> {
                        ImagePreviewsView(
                            R.drawable.readme_repeating_practice_1,
                            R.drawable.readme_repeating_practice_2,
                            R.drawable.readme_repeating_practice_3,
                        )
                    }


                    is ReadmeSheetVM.Paragraph.RepeatingsPractice2 -> {
                        ImagePreviewsView(
                            R.drawable.readme_repeating_practice_4,
                            R.drawable.readme_repeating_practice_5,
                        )
                    }

                    is ReadmeSheetVM.Paragraph.ChecklistsExamples -> {
                        ImagePreviewsView(
                            R.drawable.readme_checklists_1,
                            R.drawable.readme_checklists_2,
                            R.drawable.readme_checklists_3,
                        )
                    }

                    is ReadmeSheetVM.Paragraph.ChecklistsPractice1 -> {
                        ImagePreviewsView(
                            R.drawable.readme_checklists_practice_1,
                            R.drawable.readme_checklists_practice_2,
                            R.drawable.readme_checklists_practice_3,
                            R.drawable.readme_checklists_practice_4,
                            R.drawable.readme_checklists_practice_5,
                            R.drawable.readme_checklists_practice_6,
                            R.drawable.readme_checklists_practice_7,
                        )
                    }

                    is ReadmeSheetVM.Paragraph.ChecklistsPractice2 -> {
                        ImagePreviewsView(
                            R.drawable.readme_checklists_practice_8,
                            R.drawable.readme_checklists_practice_9,
                        )
                    }

                    is ReadmeSheetVM.Paragraph.GoalsExamples -> {
                        ImagePreviewsView(
                            R.drawable.readme_goals_1,
                        )
                    }

                    is ReadmeSheetVM.Paragraph.CalendarExamples -> {
                        ImagePreviewsView(
                            R.drawable.readme_calendar_1,
                            R.drawable.readme_calendar_2,
                        )
                    }

                    is ReadmeSheetVM.Paragraph.AskAQuestion -> {

                        MyListView__ItemView(
                            isFirst = true,
                            isLast = true,
                            modifier = Modifier
                                .padding(top = 24.dp),
                        ) {
                            MyListView__ItemView__ButtonView(text = paragraph.title) {
                                askAQuestion(subject = paragraph.subject)
                            }
                        }
                    }
                }
            }

            ZStack(
                modifier = Modifier
                    .navigationBarsPadding(),
            )
        }
    }
}

@Composable
private fun PTitleView(
    text: String,
    prevP: ReadmeSheetVM.Paragraph?,
) {
    val paddingTop: Dp = when {
        prevP == null -> 0.dp // todo
        prevP.isSlider -> 36.dp
        prevP is ReadmeSheetVM.Paragraph.Text -> 40.dp
        else -> throw Exception()
    }
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = H_PADDING)
            .padding(top = paddingTop),
        color = c.text,
        fontWeight = Fs__TITLE_FONT_WEIGHT,
        fontSize = Fs__TITLE_FONT_SIZE,
    )
}

@Composable
private fun PTextView(
    text: String,
    prevP: ReadmeSheetVM.Paragraph?,
) {
    val paddingTop: Dp = when {
        prevP == null -> 13.dp
        prevP.isSlider -> 10.dp
        prevP is ReadmeSheetVM.Paragraph.Title -> 18.dp
        prevP is ReadmeSheetVM.Paragraph.Text -> 12.dp // 1.62 * 1.62
        prevP is ReadmeSheetVM.Paragraph.TextHighlight -> 18.dp // Equals to paragraph padding
        else -> throw Exception()
    }
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = H_PADDING)
            .padding(top = paddingTop),
        color = c.text,
        lineHeight = pTextLineHeight,
        fontWeight = FontWeight.Normal,
    )
}

@Composable
private fun PTextHighlightView(
    text: String,
    prevP: ReadmeSheetVM.Paragraph?,
) {
    val paddingTop: Dp = when {
        prevP == null -> throw Exception()
        prevP.isSlider -> 18.dp
        prevP is ReadmeSheetVM.Paragraph.Text -> 19.dp
        prevP is ReadmeSheetVM.Paragraph.ListDash -> 20.dp
        else -> throw Exception()
    }
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = paddingTop)
            .padding(horizontal = H_PADDING - 2.dp)
            .clip(squircleShape)
            .background(c.blue)
            .padding(horizontal = 14.dp)
            .padding(top = 11.dp, bottom = 11.dp),
        color = c.text,
        lineHeight = pTextLineHeight,
        fontWeight = FontWeight.Normal,
    )
}

@Composable
private fun PListDashedView(
    items: List<String>,
) {
    VStack {

        items.forEach { item ->

            HStack(
                modifier = Modifier
                    .padding(top = 8.dp),
            ) {

                Icon(
                    painter = painterResource(R.drawable.sf_minus_medium_regular),
                    contentDescription = item,
                    modifier = Modifier
                        .padding(start = H_PADDING, top = 5.dp)
                        .size(12.dp),
                    tint = c.white,
                )

                Text(
                    text = item,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = H_PADDING),
                    color = c.white,
                    lineHeight = pTextLineHeight,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}

private val imageBorderColor = ColorRgba(96, 96, 96).toColor()
private val imageSliderShape = SquircleShape(len = 90f, angleParam = 10f)

private val imageSliderEnterAnimation: EnterTransition = slideInVertically(
    animationSpec = spring(
        stiffness = Spring.StiffnessMedium,
        visibilityThreshold = IntOffset.VisibilityThreshold,
    ),
    initialOffsetY = { it },
)

private val imageSliderExitAnimation: ExitTransition = slideOutVertically(
    animationSpec = spring(
        stiffness = Spring.StiffnessMedium,
        visibilityThreshold = IntOffset.VisibilityThreshold,
    ),
    targetOffsetY = { it },
)

@Composable
private fun ImagePreviewsView(
    vararg resIds: Int,
    paddingTop: Dp = 20.dp,
) {
    val scrollState = rememberScrollState()
    HStack(
        modifier = Modifier
            .padding(top = paddingTop, bottom = 8.dp)
            .padding(horizontal = imagesHBlock)
            .horizontalScroll(scrollState),
    ) {

        resIds.forEach { resId ->

            Image(
                painter = painterResource(resId),
                modifier = Modifier
                    .height(250.dp)
                    .padding(horizontal = imagesHBetween)
                    .clip(imagesShape)
                    .border(1.dp, imageBorderColor, shape = imagesShape)
                    .clickable {
                        showImagesSlider(*resIds)
                    },
                contentDescription = "Screenshot",
                contentScale = ContentScale.Fit,
            )
        }
    }
}

private fun showImagesSlider(
    vararg resIds: Int,
) {

    WrapperView.Layer(
        enterAnimation = imageSliderEnterAnimation,
        exitAnimation = imageSliderExitAnimation,
        alignment = Alignment.BottomCenter,
        onClose = {},
        content = { layer ->
            VStack(
                modifier = Modifier
                    .background(c.black)
                    .fillMaxSize()
                    .padding(top = statusBarHeight)
                    .pointerInput(Unit) { },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                val scrollState = rememberScrollState()
                HStack(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(scrollState),
                ) {

                    Padding(horizontal = 8.dp)

                    resIds.forEach { resId ->
                        Image(
                            painter = painterResource(resId),
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .fillMaxHeight()
                                .clip(imageSliderShape)
                                .border(1.dp, imageBorderColor, shape = imageSliderShape),
                            contentDescription = "Screenshot",
                            contentScale = ContentScale.FillHeight,
                        )
                    }

                    Padding(horizontal = 8.dp)
                }

                Text(
                    text = "Close",
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .padding(vertical = 8.dp)
                        .align(Alignment.End)
                        .navigationBarsPadding()
                        .clip(roundedShape)
                        .clickable {
                            layer.close()
                        }
                        .padding(horizontal = 14.dp)
                        .padding(top = 6.dp, bottom = 7.dp),
                    color = c.textSecondary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Light,
                )
            }
        }
    ).show()
}
