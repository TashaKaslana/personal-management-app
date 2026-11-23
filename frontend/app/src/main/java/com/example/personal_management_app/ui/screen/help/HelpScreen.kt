package com.example.personal_management_app.ui.screen.help

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.personal_management_app.ui.components.help.FaqAccordionCard
import com.example.personal_management_app.ui.components.help.FeedbackBottomSheetContent
import com.example.personal_management_app.ui.components.help.HelpEmptyState
import com.example.personal_management_app.ui.components.help.HelpPrivacyDialog
import com.example.personal_management_app.ui.components.help.HelpSearchBar
import com.example.personal_management_app.ui.components.help.HelpSupportBanner
import com.example.personal_management_app.ui.components.help.HelpTermsDialog
import com.example.personal_management_app.ui.components.help.HelpTopBar
import com.example.personal_management_app.ui.components.help.HelpVersionDialog
import com.example.personal_management_app.viewmodel.HelpFeedbackViewModel
import com.example.personal_management_app.viewmodel.HelpfulRating

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    navController: NavController,
    viewModel: HelpFeedbackViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filteredFaqs by viewModel.filteredFaqs.collectAsStateWithLifecycle()
    val expandedFaqIds by viewModel.expandedFaqIds.collectAsStateWithLifecycle()
    val faqRatings by viewModel.faqRatings.collectAsStateWithLifecycle()
    val faqRatingFeedback by viewModel.faqRatingFeedback.collectAsStateWithLifecycle()
    val faqRatingSubmitted by viewModel.faqRatingSubmitted.collectAsStateWithLifecycle()

    val showPrivacyDialog by viewModel.showPrivacyDialog.collectAsStateWithLifecycle()
    val showTermsDialog by viewModel.showTermsDialog.collectAsStateWithLifecycle()
    val showVersionDialog by viewModel.showVersionDialog.collectAsStateWithLifecycle()

    val showFeedbackSheet by viewModel.showFeedbackSheet.collectAsStateWithLifecycle()
    val feedbackType by viewModel.feedbackType.collectAsStateWithLifecycle()
    val feedbackContent by viewModel.feedbackContent.collectAsStateWithLifecycle()
    val attachDeviceInfo by viewModel.attachDeviceInfo.collectAsStateWithLifecycle()
    val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbarMessage()
        }
    }

    Scaffold(
        topBar = {
            HelpTopBar(
                onBackClick = { navController.popBackStack() },
                onShowPrivacy = { viewModel.showPrivacyDialog(true) },
                onShowTerms = { viewModel.showTermsDialog(true) },
                onShowVersion = { viewModel.showVersionDialog(true) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(modifier = Modifier.padding(top = 4.dp)) {
                    HelpSearchBar(
                        query = searchQuery,
                        onQueryChange = viewModel::onSearchQueryChange
                    )
                }
            }

            item {
                HelpSupportBanner(
                    onOpenFeedback = { viewModel.showFeedbackSheet(true) }
                )
            }

            item {
                Text(
                    text = if (searchQuery.isBlank()) "Câu hỏi thường gặp & Hướng dẫn" else "Kết quả tìm kiếm (${filteredFaqs.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (filteredFaqs.isEmpty()) {
                item {
                    HelpEmptyState(
                        searchQuery = searchQuery,
                        onOpenFeedback = { viewModel.showFeedbackSheet(true) }
                    )
                }
            } else {
                items(filteredFaqs, key = { it.id }) { faq ->
                    val isExpanded = faq.id in expandedFaqIds
                    val rating = faqRatings[faq.id] ?: HelpfulRating.NONE
                    val ratingFeedback = faqRatingFeedback[faq.id] ?: ""
                    val isRatingSubmitted = faq.id in faqRatingSubmitted

                    FaqAccordionCard(
                        faq = faq,
                        isExpanded = isExpanded,
                        rating = rating,
                        ratingFeedback = ratingFeedback,
                        isRatingSubmitted = isRatingSubmitted,
                        onToggleExpand = { viewModel.toggleFaqExpanded(faq.id) },
                        onRate = { viewModel.rateFaq(faq.id, it) },
                        onRatingFeedbackChange = { viewModel.onFaqRatingFeedbackChange(faq.id, it) },
                        onSubmitRatingFeedback = { viewModel.submitFaqRatingFeedback(faq.id) }
                    )
                }
            }

            item {
                Box(modifier = Modifier.padding(bottom = 24.dp))
            }
        }
    }

    if (showFeedbackSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.showFeedbackSheet(false) },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            FeedbackBottomSheetContent(
                feedbackType = feedbackType,
                feedbackContent = feedbackContent,
                attachDeviceInfo = attachDeviceInfo,
                isSubmitting = isSubmitting,
                deviceInfoPreview = viewModel.getDeviceInformation(),
                onTypeChange = viewModel::setFeedbackType,
                onContentChange = viewModel::onFeedbackContentChange,
                onToggleAttachDevice = viewModel::toggleAttachDeviceInfo,
                onSubmit = viewModel::submitFeedback,
                onDismiss = { viewModel.showFeedbackSheet(false) }
            )
        }
    }

    if (showPrivacyDialog) {
        HelpPrivacyDialog(
            onDismiss = { viewModel.showPrivacyDialog(false) }
        )
    }

    if (showTermsDialog) {
        HelpTermsDialog(
            onDismiss = { viewModel.showTermsDialog(false) }
        )
    }

    if (showVersionDialog) {
        HelpVersionDialog(
            onDismiss = { viewModel.showVersionDialog(false) }
        )
    }
}
