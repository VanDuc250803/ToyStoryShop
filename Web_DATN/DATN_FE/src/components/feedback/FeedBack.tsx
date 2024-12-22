import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { useNavigate } from 'react-router-dom';

const FeedbackMenu: React.FC = () => {
  const navigate = useNavigate();
  
  const handleAPP = () => {
    navigate('/admin/FeedbackApp');
  };

  // Hàm điều hướng đến màn hình FeedbackKH
  const handleKH = () => {
    navigate('/admin/FeedbackKH');
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Chọn loại phản hồi</Text>
      <TouchableOpacity
        style={styles.button}
        onPress={handleKH} // Sử dụng handleAPP để điều hướng
      >
        <Text style={styles.buttonText}>Đánh giá sản phẩm</Text>
      </TouchableOpacity>
      <TouchableOpacity
        style={styles.button}
        onPress={handleAPP} // Sử dụng handleKH để điều hướng
      >
        <Text style={styles.buttonText}>Đánh giá ứng dụng</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#f5f5f5',
    padding: 20,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    marginBottom: 30,
    textAlign: 'center',
    color: '#333',
    fontFamily: 'Arial', // Phông chữ đẹp hơn
  },
  button: {
    padding: 15,
    backgroundColor: '#6200ee',
    borderRadius: 15, // Bo góc lớn hơn
    marginBottom: 20,
    width: '80%',
    alignItems: 'center',
    elevation: 5, // Bóng đổ nhẹ cho button
    shadowColor: '#000', // Màu bóng đổ
    shadowOffset: { width: 0, height: 4 }, // Định vị bóng đổ
    shadowOpacity: 0.2, // Độ mờ của bóng đổ
    shadowRadius: 6, // Độ lan tỏa bóng đổ
  },
  buttonText: {
    fontSize: 18,
    color: '#fff',
    fontWeight: 'bold',
  },
});

export default FeedbackMenu;
