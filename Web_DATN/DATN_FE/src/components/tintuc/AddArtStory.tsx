import React from 'react';
import { Form, Input, Button, DatePicker, Upload, message, Row, Col, Space, Typography } from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import axios from 'axios';
import moment from 'moment';
import { upload } from "../../service/upload"; // Import upload service từ service
import { useNavigate } from 'react-router-dom'; 

// Định nghĩa kiểu dữ liệu ArtStory
interface ArtStory {
    _id?: string;
    title: string;
    author?: string;
    date?: string | moment.Moment;  // Cập nhật kiểu date cho phép cả string và moment
    description?: string;
    content?: string;
    caption?: string;
    imageUrl?: string[];
}

const { Title } = Typography;

const AddArtStory: React.FC = () => {
    const [form] = Form.useForm();
    const [imageFiles, setImageFiles] = React.useState<any[]>([]);
    const [imageUrls, setImageUrls] = React.useState<string[]>([]);
    const navigate = useNavigate();

    // const uploadImages = async (files: any) => {
    //     const formData = new FormData();
    //     files.forEach((file: any) => {
    //         formData.append("images", file);
    //     });

    //     try {
    //         const res = await upload(formData);
    //         const imageUrls = res.payload.map((item: any) => item.url);
    //         return imageUrls;
    //     } catch (error) {
    //         message.error('Lỗi khi upload hình ảnh!');
    //         return [];
    //     }
    // };
    const uploadImages = async (files: any) => {
        const formData = new FormData();
        files.forEach((file: any) => {
            formData.append("images", file);
        });

        try {
            const res = await upload(formData);
            console.log("Upload response:", res); // Kiểm tra toàn bộ response

            if (!res.payload || !Array.isArray(res.payload)) {
                throw new Error("Invalid response payload!");
            }

            const imageUrls = res.payload.map((item: any) => item.url);
            console.log("Image URLs:", imageUrls);
            return imageUrls;
        } catch (error) {
            console.error("Upload error:", error);
            message.error('Lỗi khi upload hình ảnh!');
            return [];
        }
    };



    // const handleImageChange = async (info: any) => {
    //     if (info.file.status === 'done') {
    //         const urls = await uploadImages([info.file.originFileObj]);
    //         setImageUrls((prevUrls) => [...prevUrls, ...urls]);
    //     }
    // };
  
    const handleImageChange = async (info: any) => {
        const { file } = info; // Lấy file từ info
        console.log("File đang upload:", file);
    
        const urls = await uploadImages([file]);
        if (urls.length > 0) {
            setImageUrls((prevUrls) => [...prevUrls, ...urls]);
            console.log("Danh sách URL ảnh:", [...imageUrls, ...urls]);
        }
    };
    


    // const handleSave = async (values: ArtStory) => {
    //     try {
    //         // Nếu date là đối tượng moment, chuyển thành chuỗi ISO
    //         if (values.date && moment.isMoment(values.date)) {
    //             values.date = values.date.format('YYYY-MM-DD');
    //         }

    //         values.imageUrl = imageUrls;

    //         await axios.post('http://localhost:28017/artstories', values);
    //         message.success('Thêm mới thành công!');
    //         form.resetFields();
    //         setImageUrls([]);
    //     } catch (error) {
    //         message.error('Lỗi khi lưu dữ liệu!');
    //     }
    // };
    const handleSave = async (values: ArtStory) => {
        try {
            if (values.date && moment.isMoment(values.date)) {
                values.date = values.date.format('YYYY-MM-DD');
            }

            // Gán imageUrls vào values
            values.imageUrl = imageUrls;
            console.log("Dữ liệu gửi lên backend:", values);
            console.log("Form values:", values); // Kiểm tra giá trị trước khi gửi
            await axios.post('http://localhost:28017/artstories', values);

            message.success('Thêm mới thành công!');
            form.resetFields();
            setImageUrls([]);
            navigate('/admin/news');
        } catch (error) {
            console.error("Save error:", error);
            message.error('Lỗi khi lưu dữ liệu!');
        }
    };




    return (
        <div style={{ padding: '20px', backgroundColor: '#f0f2f5', borderRadius: '10px', boxShadow: '0 4px 8px rgba(0,0,0,0.1)', maxWidth: '1000px', margin: '20px auto' }}>
            <Title level={2} style={{ textAlign: 'center', marginBottom: '20px' }}>Thêm Mới ArtStory</Title>
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
                            <DatePicker
                                style={{ width: '100%' }}
                                placeholder="Chọn ngày"
                                value={form.getFieldValue('date') ? moment(form.getFieldValue('date')) : undefined}  // Lấy giá trị date từ form
                                onChange={(date) => form.setFieldsValue({ date })}  // Cập nhật giá trị date khi thay đổi
                            />
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
                                    handleImageChange({ file }); // Gọi hàm xử lý file upload
                                    return false; // Ngăn Ant Design tự gửi file
                                }}
                                listType="picture-card"
                                accept="image/*"
                            >
                                <Button icon={<UploadOutlined />}>Chọn hình ảnh</Button>
                            </Upload>


                        </Form.Item>
                    </Col>
                </Row>

                <Row gutter={24}>
                    <Col span={24}>
                        <Form.Item label="Ảnh đã upload">
                            {imageUrls.length > 0 ? (
                                <div style={{ display: 'flex', flexWrap: 'wrap' }}>
                                    {imageUrls.map((url, index) => (
                                        <img key={index} src={url} alt={`ArtStory image ${index + 1}`} style={{ width: '100px', height: '100px', objectFit: 'cover', margin: '5px' }} />
                                    ))}
                                </div>
                            ) : (
                                <p>Chưa có ảnh nào được chọn.</p>
                            )}
                        </Form.Item>
                    </Col>
                </Row>

                <Form.Item>
                    <Space style={{ width: '100%' }} direction="vertical">
                        <Button type="primary" htmlType="submit" style={{ width: '100%' }}>Thêm mới ArtStory</Button>
                    </Space>
                </Form.Item>
            </Form>
        </div>
    );
};

export default AddArtStory;
