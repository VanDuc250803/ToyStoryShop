import React, { useState, useEffect } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet } from 'react-native';
import axios from 'axios';

// Định nghĩa kiểu dữ liệu Feedback
interface Feedback {
  id: string;
  cusId: string;
  start: number;
  content: string;
  dateFeed: string;
}

const FeedbackApp: React.FC = () => {
  const [feedbacks, setFeedbacks] = useState<Feedback[]>([]); // Danh sách các phản hồi
  const [loading, setLoading] = useState<boolean>(false); // Trạng thái tải dữ liệu
  const [deletingId, setDeletingId] = useState<string | null>(null); // Lưu ID của phản hồi đang xóa
  const [error, setError] = useState<string | null>(null); // Thông báo lỗi nếu có

  // Fetch dữ liệu phản hồi từ API
  useEffect(() => {
    setLoading(true);
    axios
      .get('http://localhost:28017/feebackapps') // Thay bằng URL API thực tế
      .then(response => {
        setFeedbacks(response.data);
      })
      .catch(error => {
        console.error('Error fetching feedbacks:', error);
      })
      .finally(() => setLoading(false));
  }, []);

  // Hàm xóa phản hồi
  const handleDelete = (id: string) => {
    setDeletingId(id); // Đánh dấu phản hồi đang xóa
    setError(null); // Reset lỗi trước khi xóa

    axios
      .delete(`http://localhost:28017/feebackapps/${id}`)
      .then(response => {
        setFeedbacks(prev => prev.filter(feedback => feedback.id !== id)); // Cập nhật lại danh sách sau khi xóa
        setError('Phản hồi đã được xóa thành công!'); // Thông báo thành công
      })
      .catch(error => {
        console.error('Error deleting feedback:', error);
        setError('Có lỗi xảy ra khi xóa phản hồi.'); // Thông báo lỗi nếu có
      })
      .finally(() => {
        setDeletingId(null); // Reset trạng thái xóa
      });
  };

  // Render từng phản hồi
  const renderFeedbackItem = ({ item }: { item: Feedback }) => (
    <View style={styles.feedbackItem}>
      <Text style={styles.feedbackText}>
        <Text style={styles.feedbackTextBold}>Khách hàng: </Text>{item.cusId}
      </Text>

      <Text style={styles.feedbackText}>
        <Text style={styles.feedbackTextBold}>Sao: </Text>{item.start}
      </Text>

      <Text style={styles.feedbackText}>
        <Text style={styles.feedbackTextBold}>Nội dung: </Text>{item.content}
      </Text>

      <Text style={styles.feedbackText}>
        <Text style={styles.feedbackTextBold}>Ngày: </Text>
        {new Date(item.dateFeed).toLocaleDateString()}
      </Text>
      
      {/* Hiển thị thông báo lỗi hoặc thành công */}
      {error && deletingId === item.id && (
        <Text style={styles.errorText}>{error}</Text>
      )}

      {/* Nút Xóa ở góc phải */}
      <TouchableOpacity
        onPress={() => handleDelete(item.id)}
        disabled={deletingId === item.id} // Vô hiệu hóa nút khi đang xóa
        style={styles.deleteButton}
      >
        <Text style={styles.deleteButtonText}>
          {deletingId === item.id ? 'Đang xóa...' : 'Delete'}
        </Text>
      </TouchableOpacity>
    </View>
  );

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Quản Lý Phản Hồi</Text>

      {/* Hiển thị danh sách phản hồi */}
      {loading ? (
        <Text>Đang tải dữ liệu...</Text>
      ) : (
        <FlatList
          data={feedbacks}
          renderItem={renderFeedbackItem}
          keyExtractor={item => item.id}
          contentContainerStyle={styles.listContainer}
        />
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F9F9F9',
    padding: 20,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
    textAlign: 'center',
    marginBottom: 20,
  },
  feedbackItem: {
    backgroundColor: '#FFF',
    padding: 15,
    borderRadius: 8,
    marginBottom: 15,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
    position: 'relative', // Để có thể đặt nút xóa ở góc phải
  },
  feedbackText: {
    fontSize: 16,
    color: '#555',
    marginBottom: 5,
  },
  feedbackTextBold: {
    fontSize: 16,
    fontWeight: 'bold',  // In đậm tiêu đề mà không làm thay đổi cấu trúc dòng
    color: '#333',  // Màu sắc tiêu đề nổi bật
  },
  listContainer: {
    paddingBottom: 20,
  },
  deleteButton: {
    position: 'absolute', // Đặt nút xóa ở góc phải
    top: 10,
    right: 10,
    backgroundColor: '#FF5252',
    padding: 10,
    borderRadius: 8,
    justifyContent: 'center',
    alignItems: 'center',
  },
  deleteButtonText: {
    color: '#FFF',
    fontSize: 14,
  },
  errorText: {
    color: 'red',
    marginTop: 5,
    fontSize: 14,
  },
});

export default FeedbackApp;
