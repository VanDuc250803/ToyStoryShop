import React, { useState, useEffect } from 'react';
import { View, Text, FlatList, Button, StyleSheet, Modal } from 'react-native';
import axios from 'axios';

// Interface cho sản phẩm
interface Product {
  // Tên sản phẩm
  prodId: string;  // ID sản phẩm
}

// Interface cho Feedback
interface Feedback {
  id: string;
  cusId: string;
  prodId: string;  // prodId là ID sản phẩm (chuỗi)
  start: number;
  dateFeed: string;
}

const FeedbackKH: React.FC = () => {
  const [feedbacks, setFeedbacks] = useState<Feedback[]>([]);
  const [products, setProducts] = useState<Record<string, Product>>({}); // Lưu sản phẩm theo prodId
  const [modalVisible, setModalVisible] = useState(false);
  const [modalMessage, setModalMessage] = useState('');

  // Fetch dữ liệu phản hồi từ API
  useEffect(() => {
    axios.get('http://localhost:28017/feedbacks')
      .then(response => {
        setFeedbacks(response.data);
        // Sau khi nhận được phản hồi, gọi API lấy thông tin sản phẩm cho từng prodId
        // Lấy danh sách các prodId từ phản hồi
        const productIds = response.data.map((feedback: Feedback) => feedback.prodId);

        // Loại bỏ trùng lặp bằng cách sử dụng filter
        const uniqueProductIds = productIds.filter((value: string, index: number, self: string[]) => self.indexOf(value) === index);

        // Tiến hành lấy dữ liệu cho từng sản phẩm
        uniqueProductIds.forEach((prodId: string) => {
          axios.get(`http://localhost:28017/products/${prodId}`)
            .then(res => {
              setProducts(prev => ({ ...prev, [prodId]: res.data }));
            })
            .catch(error => {
              console.error('Error fetching product:', error);
            });
        });

      })
      .catch(error => {
        console.error('Error fetching feedbacks:', error);
        setModalMessage('Không thể tải dữ liệu phản hồi.');
        setModalVisible(true);
      });
  }, []);

  // Hàm xử lý xóa phản hồi
  const handleDelete = (id: string) => {
    axios.delete(`http://localhost:28017/feedbacks/${id}`)
      .then(response => {
        console.log('Phản hồi đã bị xóa:', response.data);
        setFeedbacks(prev => prev.filter(feedback => feedback.id !== id));
        setModalMessage('Phản hồi đã được xóa.');
        setModalVisible(true);
      })
      .catch(error => {
        console.error('Lỗi khi xóa phản hồi:', error);
        setModalMessage('Có lỗi xảy ra khi xóa phản hồi.');
        setModalVisible(true);
      });
  };

  // Hàm render từng phản hồi trong danh sách
  const renderFeedbackItem = ({ item }: { item: Feedback }) => {
    // Lấy thông tin sản phẩm từ `products` bằng cách sử dụng `prodId`
    const product = products[item.prodId]; // Lấy sản phẩm tương ứng với prodId của feedback

    // Hiển thị prodId trực tiếp (thay vì lấy tên sản phẩm)
    const productId = item.prodId;

    return (
      <View style={styles.feedbackItem}>
        <Text style={styles.feedbackText}>
          <Text style={styles.label}>Khách hàng:</Text> {item.cusId}
        </Text>
        <Text style={styles.feedbackText}>
          <Text style={styles.label}>Sản phẩm ID:</Text> {productId} {/* Hiển thị prodId */}
        </Text>
        <Text style={styles.feedbackText}>
          <Text style={styles.label}>Sao:</Text> {item.start}
        </Text>
        <Text style={styles.feedbackText}>
          <Text style={styles.label}>Ngày:</Text> {new Date(item.dateFeed).toLocaleDateString()}
        </Text>
        <View style={styles.actions}>
          <Button
            title="Delete"
            onPress={() => handleDelete(item.id)}
            color="#FF5252"
          />
        </View>
      </View>
    );
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Quản Lý Phản Hồi</Text>
      <FlatList
        data={feedbacks}
        renderItem={renderFeedbackItem}
        keyExtractor={item => item.id}  // Đảm bảo rằng 'id' là chuỗi hoặc một giá trị duy nhất
        contentContainerStyle={styles.listContainer}
      />
      <Modal
        animationType="slide"
        transparent={true}
        visible={modalVisible}
        onRequestClose={() => setModalVisible(false)}>
        <View style={styles.modalView}>
          <Text style={styles.modalText}>{modalMessage}</Text>
          <Button title="Đóng" onPress={() => setModalVisible(false)} />
        </View>
      </Modal>
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
  listContainer: {
    paddingBottom: 20,
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
  },
  feedbackText: {
    fontSize: 16,
    color: '#555',
    marginBottom: 5,
  },
  label: {
    fontWeight: 'bold',
    color: '#333',
  },
  actions: {
    marginTop: 10,
    flexDirection: 'row',
    justifyContent: 'flex-end',
  },
  modalView: {
    backgroundColor: 'white',
    padding: 20,
    marginTop: 100,
    marginHorizontal: 40,
    borderRadius: 8,
    alignItems: 'center',
  },
  modalText: {
    fontSize: 16,
    marginBottom: 20,
  },
});

export default FeedbackKH;
