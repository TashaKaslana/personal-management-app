package com.example.personal_management_app.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personal_management_app.repositories.FeedbackRepository
import com.example.personal_management_app.utils.matchesSearch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FaqItem(
    val id: String,
    val title: String,
    val summary: String,
    val steps: List<String>,
    val note: String
)

enum class HelpfulRating {
    NONE, YES, NO
}

@HiltViewModel
class HelpFeedbackViewModel @Inject constructor(
    private val feedbackRepository: FeedbackRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val allFaqs = listOf(
        FaqItem(
            id = "faq_formatting",
            title = "Tạo, chỉnh sửa và định dạng văn bản ghi chú",
            summary = "Hướng dẫn tạo mới ghi chú, chọn kiểu tiêu đề, căn lề và định dạng nội dung văn bản trực quan.",
            steps = listOf(
                "Nhấn vào nút dấu (+) màu vàng ở góc dưới cùng màn hình chính để mở trang tạo ghi chú mới.",
                "Nhập tiêu đề cho ghi chú và soạn thảo nội dung theo nhu cầu.",
                "Sử dụng thanh công cụ phía dưới để thay đổi cỡ chữ (Tiêu đề H1, H2, nội dung), in đậm, in nghiêng hoặc chọn màu nền ghi chú.",
                "Nhấn nút mũi tên quay lại ở góc trên bên trái, hệ thống sẽ tự động lưu lại toàn bộ nội dung."
            ),
            note = "Mọi thay đổi trên ghi chú đều được tự động lưu vào cơ sở dữ liệu SQLite ngay khi bạn thao tác, không lo mất dữ liệu khi vô tình đóng ứng dụng."
        ),
        FaqItem(
            id = "faq_checklist",
            title = "Quản lý danh sách công việc (Checklist) và Ghim ghi chú",
            summary = "Cách thêm ô kiểm công việc cần hoàn thành và đưa ghi chú quan trọng lên vị trí ưu tiên.",
            steps = listOf(
                "Trong màn hình soạn thảo ghi chú, nhấn vào biểu tượng Danh sách kiểm (Checklist) trên thanh công cụ.",
                "Nhập nội dung các công việc cần làm trên từng dòng.",
                "Khi hoàn thành một công việc, chạm vào ô vuông để đánh dấu hoàn tất.",
                "Để ghim ghi chú quan trọng lên đầu danh sách, nhấn vào biểu tượng Ghim (Pin) ở góc trên bên phải thanh công cụ."
            ),
            note = "Các ghi chú được ghim sẽ luôn cố định ở phần trên cùng của danh sách ghi chú, giúp bạn dễ dàng theo dõi mục tiêu ưu tiên nhất."
        ),
        FaqItem(
            id = "faq_tags",
            title = "Phân loại ghi chú bằng Nhãn (Tags) và Màu sắc",
            summary = "Sắp xếp và nhóm các ghi chú khoa học theo chủ đề thông qua hệ thống Nhãn và màu nền sinh động.",
            steps = listOf(
                "Mở menu ngăn kéo bên trái (Sidebar) và chọn mục 'Nhãn' để tạo mới hoặc quản lý nhãn theo chủ đề (Công việc, Học tập, Du lịch...).",
                "Mỗi nhãn có thể được chọn màu sắc riêng biệt để dễ dàng nhận biết.",
                "Khi soạn thảo ghi chú, bấm vào biểu tượng Nhãn để gắn một hoặc nhiều nhãn tương ứng.",
                "Chọn màu sắc nền ghi chú từ bảng màu để tạo dấu ấn trực quan cho từng chủ đề."
            ),
            note = "Một ghi chú có thể gắn nhiều nhãn khác nhau. Bạn có thể bấm vào một nhãn trong menu để lọc và hiển thị tất cả ghi chú thuộc nhãn đó."
        ),
        FaqItem(
            id = "faq_reminders",
            title = "Đặt lời nhắc thông báo (Reminders) & Xử lý khi không hiện",
            summary = "Hẹn giờ nhắc việc theo lịch trình và các bước khắc phục sự cố hệ thống không đổ chuông báo thức.",
            steps = listOf(
                "Mở ghi chú cần đặt lịch, nhấn vào biểu tượng Đồng hồ (Lời nhắc) ở góc trên bên phải.",
                "Chọn ngày, giờ cụ thể bạn muốn nhận thông báo rồi nhấn 'Lưu'.",
                "Nếu đến giờ mà không nhận được thông báo: Vào Cài đặt điện thoại -> Ứng dụng -> Personal Management App -> Bật 'Quyền thông báo'.",
                "Tắt chế độ 'Tối ưu hóa pin' (Battery Optimization) đối với ứng dụng và bật quyền 'Tự khởi chạy' (Autostart) trên các dòng máy như Xiaomi, Oppo, Vivo, Samsung."
            ),
            note = "Đảm bảo điện thoại không bật chế độ 'Không làm phiền' (Do Not Disturb) trong khung giờ bạn đã lên lịch nhắc việc."
        ),
        FaqItem(
            id = "faq_trash",
            title = "Lưu trữ (Archive) và Thùng rác (Trash - Quy tắc 30 ngày)",
            summary = "Giữ màn hình chính gọn gàng bằng tính năng Lưu trữ và cơ chế hoạt động, khôi phục của Thùng rác.",
            steps = listOf(
                "Để lưu trữ: Nhấn biểu tượng Hộp lưu trữ trên thanh công cụ ghi chú. Ghi chú sẽ được cất vào mục 'Lưu trữ' mà không bị xóa dữ liệu.",
                "Để chuyển vào Thùng rác: Nhấn menu tùy chọn của ghi chú và chọn 'Xóa'.",
                "Để khôi phục hoặc xóa vĩnh viễn: Vào menu bên trái -> 'Thùng rác' -> Nhấn nút 'Khôi phục' để đưa về danh sách chính, hoặc 'Xóa vĩnh viễn' để xóa hoàn toàn.",
                "Bạn cũng có thể bấm nút 'Dọn sạch' ở thanh tiêu đề Thùng rác để xóa toàn bộ các mục ngay lập tức."
            ),
            note = "Mục trong Thùng rác sẽ được tự động xóa vĩnh viễn sau 30 ngày kể từ ngày chuyển vào. Thẻ ghi chú luôn hiển thị số ngày còn lại để bạn tiện theo dõi."
        )
    )

    private val _filteredFaqs = MutableStateFlow(allFaqs)
    val filteredFaqs = _filteredFaqs.asStateFlow()

    private val _expandedFaqIds = MutableStateFlow<Set<String>>(emptySet())
    val expandedFaqIds = _expandedFaqIds.asStateFlow()

    private val _faqRatings = MutableStateFlow<Map<String, HelpfulRating>>(emptyMap())
    val faqRatings = _faqRatings.asStateFlow()

    private val _faqRatingFeedback = MutableStateFlow<Map<String, String>>(emptyMap())
    val faqRatingFeedback = _faqRatingFeedback.asStateFlow()

    private val _faqRatingSubmitted = MutableStateFlow<Set<String>>(emptySet())
    val faqRatingSubmitted = _faqRatingSubmitted.asStateFlow()

    private val _showPrivacyDialog = MutableStateFlow(false)
    val showPrivacyDialog = _showPrivacyDialog.asStateFlow()

    private val _showTermsDialog = MutableStateFlow(false)
    val showTermsDialog = _showTermsDialog.asStateFlow()

    private val _showVersionDialog = MutableStateFlow(false)
    val showVersionDialog = _showVersionDialog.asStateFlow()

    private val _showFeedbackSheet = MutableStateFlow(false)
    val showFeedbackSheet = _showFeedbackSheet.asStateFlow()

    private val _feedbackType = MutableStateFlow("Báo lỗi kỹ thuật")
    val feedbackType = _feedbackType.asStateFlow()

    private val _feedbackContent = MutableStateFlow("")
    val feedbackContent = _feedbackContent.asStateFlow()

    private val _attachDeviceInfo = MutableStateFlow(true)
    val attachDeviceInfo = _attachDeviceInfo.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting = _isSubmitting.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        val clean = query.trim()
        if (clean.isBlank()) {
            _filteredFaqs.value = allFaqs
        } else {
            _filteredFaqs.value = allFaqs.filter { faq ->
                val fullText = "${faq.title} ${faq.summary} ${faq.steps.joinToString(" ")} ${faq.note}"
                fullText.matchesSearch(clean)
            }
        }
    }

    fun toggleFaqExpanded(id: String) {
        _expandedFaqIds.value = if (id in _expandedFaqIds.value) {
            _expandedFaqIds.value - id
        } else {
            _expandedFaqIds.value + id
        }
    }

    fun rateFaq(id: String, rating: HelpfulRating) {
        _faqRatings.value = _faqRatings.value + (id to rating)
        if (rating == HelpfulRating.YES) {
            _faqRatingSubmitted.value = _faqRatingSubmitted.value + id
        }
    }

    fun onFaqRatingFeedbackChange(id: String, text: String) {
        _faqRatingFeedback.value = _faqRatingFeedback.value + (id to text)
    }

    fun submitFaqRatingFeedback(id: String) {
        _faqRatingSubmitted.value = _faqRatingSubmitted.value + id
    }

    fun showPrivacyDialog(show: Boolean) {
        _showPrivacyDialog.value = show
    }

    fun showTermsDialog(show: Boolean) {
        _showTermsDialog.value = show
    }

    fun showVersionDialog(show: Boolean) {
        _showVersionDialog.value = show
    }

    fun showFeedbackSheet(show: Boolean) {
        _showFeedbackSheet.value = show
        if (show) {
            _feedbackContent.value = ""
        }
    }

    fun setFeedbackType(type: String) {
        _feedbackType.value = type
    }

    fun onFeedbackContentChange(content: String) {
        _feedbackContent.value = content
    }

    fun toggleAttachDeviceInfo(attach: Boolean) {
        _attachDeviceInfo.value = attach
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun getDeviceInformation(): String {
        return "Thiết bị: ${Build.MANUFACTURER} ${Build.MODEL}\n" +
               "Hệ điều hành: Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n" +
               "Phiên bản ứng dụng: 1.0.0"
    }

    fun submitFeedback() {
        val content = _feedbackContent.value.trim()
        if (content.isBlank()) {
            _snackbarMessage.value = "Vui lòng nhập nội dung mô tả phản hồi"
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            val deviceInfo = if (_attachDeviceInfo.value) getDeviceInformation() else null
            val success = feedbackRepository.submitFeedback(
                type = _feedbackType.value,
                content = content,
                deviceInfo = deviceInfo
            )
            _isSubmitting.value = false
            if (success) {
                _showFeedbackSheet.value = false
                _feedbackContent.value = ""
                _snackbarMessage.value = "Cảm ơn bạn! Phản hồi đã được gửi thành công."
            } else {
                _snackbarMessage.value = "Không thể gửi phản hồi, vui lòng thử lại sau."
            }
        }
    }
}