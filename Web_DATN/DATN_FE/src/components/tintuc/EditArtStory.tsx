import React, { useEffect, useState } from 'react';
import { Form, Input, Button, DatePicker, Upload, message, Row, Col, Space, Typography, Modal } from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom'; // Sử dụng useNavigate
import moment from 'moment';

const { Title } = Typography;

interface ArtStory {
  _id?: string;
  title: string;
  author?: string;
  date?: string | moment.Moment; 
  description?: string;
  content?: string;
  caption?: string[];
  imageUrl?: string[];
}

const Edit: React.FC = () => {
  const { id } = useParams();
  const [form] = Form.useForm();
  const [currentStory, setCurrentStory] = useState<ArtStory | null>(null);
  const [imageFiles, setImageFiles] = useState<any[]>([]);
  const navigate = useNavigate(); // Sử dụng useNavigate để thay thế history
  const [isModalVisible, setIsModalVisible] = useState(false); // Trạng thái cho Modal xác nhận

  useEffect(() => {
    const fetchStory = async () => {
      try {
        const response = await axios.get(`http://localhost:28017/artstories/${id}`);
        const data = response.data;
        setCurrentStory(data);

        if (data.date) {
          data.date = moment(data.date);
        }
        form.setFieldsValue(data);
      } catch (error) {
        message.error('Lỗi khi tải dữ liệu!');
      }
    };
    if (id) fetchStory();
  }, [id]);

  const uploadImages = async (files: any) => {
    const formData = new FormData();
    files.forEach((file: any) => {
      formData.append('images', file);
    });

    try {
      const res = await axios.post('http://localhost:28017/upload', formData);
      const imageUrls = res.data.payload.map((item: any) => item.url);
      return imageUrls;
    } catch (error) {
      message.error('Lỗi khi tải lên hình ảnh!');
      return [];
    }
  };

  const handleSave = async (values: ArtStory) => {
    try {
      if (currentStory) {
        if (values.date) {
          if (!moment.isMoment(values.date)) {
            values.date = moment(values.date);
          }
          values.date = values.date.format('YYYY-MM-DD');
        }

        const imageUrls = await uploadImages(imageFiles);
        values.imageUrl = imageUrls;

        await axios.put(`http://localhost:28017/artstories/${id}`, values);
        message.success('Cập nhật thành công!');
        navigate('/admin/news'); // Sau khi cập nhật thành công, điều hướng về trang danh sách
      }
    } catch (error) {
      message.error('Lỗi khi lưu dữ liệu!');
    }
  };

  const handleBack = () => {
    setIsModalVisible(true); // Hiển thị modal khi nhấn nút quay lại
  };

  const handleModalCancel = () => {
    setIsModalVisible(false); // Đóng modal khi nhấn "Hủy"
  };

  const handleModalOk = () => {
    setIsModalVisible(false);
    navigate('/admin/news'); // Điều hướng về trang chủ hoặc trang trước đó
  };

  return currentStory ? (
    <div style={{ padding: '20px', backgroundColor: '#f0f2f5', borderRadius: '10px', boxShadow: '0 4px 8px rgba(0,0,0,0.1)', maxWidth: '1000px', margin: '20px auto' }}>
      <Title level={2} style={{ textAlign: 'center', marginBottom: '20px' }}>Cập nhật ArtStory</Title>
      <Form form={form} onFinish={handleSave} layout="vertical">
        <Row gutter={24}>
          <Col xs={24} sm={12}>
            <Form.Item name="title" label="Tiêu đề" rules={[{ required: true, message: 'Vui lòng nhập tiêu đề!' }]}>
              <Input placeholder="Nhập tiêu đề bài viết" />
            </Form.Item>
          </Col>
          <Col xs={24} sm={12}>
            <Form.Item name="author" label="Tác giả">
              <Input placeholder="Nhập tên tác giả" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={24}>
          <Col xs={24} sm={12}>
            <Form.Item name="date" label="Ngày tạo">
              <DatePicker style={{ width: '100%' }} placeholder="Chọn ngày" />
            </Form.Item>
          </Col>
          <Col xs={24} sm={12}>
            <Form.Item name="description" label="Mô tả">
              <Input.TextArea rows={3} placeholder="Nhập mô tả bài viết" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={24}>
          <Col span={24}>
            <Form.Item name="content" label="Nội dung">
              <Input.TextArea rows={5} placeholder="Nhập nội dung bài viết" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={24}>
          <Col span={24}>
            <Form.Item name="caption" label="Chú thích">
              <Input.TextArea rows={3} placeholder="Nhập chú thích cho bài viết" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={24}>
          <Col span={24}>
            <Form.Item name="imageUrl" label="Hình ảnh">
              <Upload
                beforeUpload={(file) => {
                  setImageFiles((prev) => [...prev, file]);
                  return false;
                }}
                onRemove={(file) => {
                  setImageFiles(imageFiles.filter((f) => f !== file));
                }}
                listType="picture-card"
                fileList={imageFiles}
                accept="image/*"
              >
                <Button icon={<UploadOutlined />}>Chọn hình ảnh</Button>
              </Upload>
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={24}>
          <Col span={12}>
            <Form.Item>
              <Button type="default" style={{ width: '100%' }} onClick={handleBack}>Quay lại</Button>
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item>
              <Space style={{ width: '100%' }} direction="vertical">
                <Button type="primary" htmlType="submit" style={{ width: '100%' }}>Lưu thay đổi</Button>
              </Space>
            </Form.Item>
          </Col>
        </Row>
      </Form>

      <Modal
        title="Xác nhận"
        visible={isModalVisible}
        onOk={handleModalOk}
        onCancel={handleModalCancel}
        okText="Có"
        cancelText="Không"
      >
        <p>Bạn có chắc chắn muốn quay lại mà không lưu thay đổi?</p>
      </Modal>
    </div>
  ) : null;
};

export default Edit;
